package pixelmon.client.models.animations;

import pixelmon.client.models.animations.bird.EnumWing;
import pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;

public class ModuleTailBasic extends ModuleTail {
	
	ModelRenderer tail;

	float TailRotationLimitY;
	float TailRotationLimitZ;
	float TailSpeed;
	float TailInitY;
	float TailInitZ;
	float TailOrientation;
	
	public ModuleTailBasic(ModelRenderer tail, float TailRotationLimitY, float TailRotationLimitZ, float TailSpeed) {
		this.tail = tail;
		this.TailSpeed = TailSpeed;
		this.TailRotationLimitY = TailRotationLimitY;
		this.TailRotationLimitZ = TailRotationLimitZ;
		TailInitY = tail.rotateAngleY;
		TailInitZ = tail.rotateAngleX;


	}

	@Override
	public void walk(EntityPixelmon entity, float f, float f1, float f2, float f3, float f4) {
		tail.rotateAngleY =  MathHelper.cos(f * TailSpeed)
				* (float) Math.PI
				* f1
				* TailRotationLimitY;
		
		tail.rotateAngleX = MathHelper.cos(f * TailSpeed * 2)
				* (float) Math.PI
				* f1
				* TailRotationLimitZ;
	}

	@Override
	public void fly(EntityPixelmon entity, float f, float f1, float f2, float f3, float f4) {

	}
}
