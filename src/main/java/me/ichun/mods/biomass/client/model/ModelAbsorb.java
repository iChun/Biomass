package me.ichun.mods.biomass.client.model;

import me.ichun.mods.biomass.client.entity.EntityAbsorbBiomass;
import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.ichunutil.client.model.util.ModelHelper;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Random;

public class ModelAbsorb extends ModelBase
{
    public Random rand;
    public ArrayList<ModelRenderer> killedCubes;
    public ArrayList<Arm> arms;
    public int nextArm;
    public int armCount;

    public ModelAbsorb()
    {
    }

    public ModelAbsorb(Render render, EntityLivingBase living)
    {
        rand = new Random();
        killedCubes = ModelHelper.getModelCubesCopy(ModelHelper.getModelCubes(render), this, living);
        arms = new ArrayList<>();
        armCount = killedCubes.size();
    }

    public void tick(EntityAbsorbBiomass ent)
    {
        if(armCount > 0)
        {
            nextArm--;
            if(nextArm < 0)
            {
                nextArm = 1 + rand.nextInt(3);
                arms.add(new Arm(this));
                armCount--;
            }
        }
        arms.forEach(arm -> arm.tick(ent));
    }

    public void renderKilled(float f5)
    {
        killedCubes.forEach(renderer -> renderer.render(f5));
    }

    public void renderArms(float f5, boolean isFirstPerson)
    {
        arms.forEach(arm -> arm.render(f5, isFirstPerson));
    }

    public void clean()
    {
        killedCubes.stream().filter(renderer -> renderer.compiled).forEach(renderer ->
        {
            GLAllocation.deleteDisplayLists(renderer.displayList);
            renderer.compiled = false;
        });
        arms.forEach(Arm::clean);
    }

    public class Arm
    {
        public final ModelAbsorb parent;
        public ArrayList<ModelRenderer> parts = new ArrayList<>();
        public boolean retracting;
        public float yaw;
        public float pitch;
        public double offsetX;
        public double offsetY;
        public double offsetZ;

        public Arm(ModelAbsorb parent)
        {
            this.parent = parent;
            yaw = rand.nextFloat() * 360F;
            pitch = rand.nextFloat() * 360F;
        }

        public void tick(EntityAbsorbBiomass ent)
        {
            if(!retracting)
            {
                //add new arm
                parent.textureHeight = 8 * (1 + rand.nextInt(16));
                parent.textureWidth = 8 * (1 + rand.nextInt(16));
                ModelRenderer renderer = new ModelRenderer(parent, rand.nextInt(64), rand.nextInt(64));
                renderer.setRotationPoint((float)offsetX * 16F, (float)offsetY * 16F, (float)offsetZ * 16F);
                renderer.rotateAngleY = (float)-Math.toRadians(yaw);
                renderer.rotateAngleX = (float)Math.toRadians(pitch);
                double dd0 = ent.killed.posX - (ent.posX + offsetX);
                double dd2 = ent.killed.posZ - (ent.posZ + offsetZ);
                double dd1 = (ent.killed.getEntityBoundingBox().minY + ent.killed.getEntityBoundingBox().maxY) / 2.0D - (ent.posY + offsetY);
                double dist = Math.sqrt(dd0 * dd0 + dd1 * dd1 + dd2 * dd2);
                int size = (int)Math.min(5, Math.max(1, dist / 1.5D));

                renderer.addBox(-(size / 2F), -(size / 2F), -1F, size, size, 8);
                parts.add(renderer);
                //end add new arm

                //calc offset
                double x = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
                double y = -MathHelper.sin(pitch * 0.017453292F);
                double z = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);

                float f = MathHelper.sqrt(x * x + y * y + z * z);
                double distance = 7F / 16F;
                x = x / (double)f;
                y = y / (double)f;
                z = z / (double)f;
                offsetX += x * distance;
                offsetY += y * distance;
                offsetZ += z * distance;
                //end calc offset

                //calc next rot
                float maxInc = 30F;
                double d0 = ent.killed.posX - (ent.posX + offsetX);
                double d2 = ent.killed.posZ - (ent.posZ + offsetZ);
                double d1 = (ent.killed.getEntityBoundingBox().minY + ent.killed.getEntityBoundingBox().maxY) / 2.0D - (ent.posY + offsetY);

                if(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) < 0.5D)
                {
                    retracting = true;
                    int index = rand.nextInt(parent.killedCubes.size());
                    parts.add(parent.killedCubes.get(index));
                    parent.killedCubes.remove(index);
                }

                double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
                float ff = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                float ff1 = (float)(-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
                pitch = EntityHelper.updateRotation(pitch, ff1, maxInc) + (float)rand.nextGaussian() * maxInc / 2F;
                yaw = EntityHelper.updateRotation(yaw, ff, maxInc) + (float)rand.nextGaussian() * maxInc / 2F;
                //end calc next rot
            }
            else if(!parts.isEmpty())
            {
                int index = parts.size() > 1 ? parts.size() - 2 : 0;
                ModelRenderer last = parts.get(parts.size() - 1);
                ModelRenderer renderer = parts.get(index);
                if(parts.size() > 1)
                {
                    last.setRotationPoint(renderer.rotationPointX, renderer.rotationPointY, renderer.rotationPointZ);
                }
                GLAllocation.deleteDisplayLists(renderer.displayList);
                renderer.compiled = false;
                parts.remove(index);
            }
        }

        public void render(float f5, boolean isFirstPerson)
        {
            for(int i = parts.size() - 1; i >= 0; i--)
            {
                ModelRenderer renderer = parts.get(i);
                if(isFirstPerson)
                {
                    GlStateManager.color(1F, 1F, 1F, MathHelper.clamp((i - 3) / (float)Biomass.config.firstPersonBiomassAbsorptionAlphaCount, 0F, 1F));
                    if(retracting && i == parts.size() - 1 && parts.size() < 10)
                    {
                        continue;
                    }
                }
                renderer.render(f5);
            }
        }

        public void clean()
        {
            parts.stream().filter(renderer -> renderer.compiled).forEach(renderer ->
            {
                GLAllocation.deleteDisplayLists(renderer.displayList);
                renderer.compiled = false;
            });
        }
    }
}
