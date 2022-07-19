package kittehmod.ceilands.client.renderer;

import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import kittehmod.ceilands.entity.ModBoat;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CeilandsBoatRenderer extends EntityRenderer<ModBoat>
{
	/** instance of ModelBoat for rendering */
	private final Map<ModBoat.Type, Pair<ResourceLocation, BoatModel>> boatResources;

	public CeilandsBoatRenderer(EntityRendererProvider.Context context, boolean isChest) {
		super(context);
		this.shadowRadius = 0.8F;
		this.boatResources = Stream.of(ModBoat.Type.values()).collect(ImmutableMap.toImmutableMap((p_173938_) -> {
			return p_173938_;
		}, (p_234575_) -> {
			return Pair.of(new ResourceLocation("ceilands", getTextureLocation(p_234575_, isChest)), this.createBoatModel(context, p_234575_, isChest));
		}));
	}

	private BoatModel createBoatModel(EntityRendererProvider.Context context, ModBoat.Type boatType, boolean isChest) {
		ModelLayerLocation modellayerlocation = isChest ? createChestBoatModelName(boatType) : createBoatModelName(boatType);
		return new BoatModel(context.bakeLayer(modellayerlocation), isChest);
	}

	private static String getTextureLocation(ModBoat.Type boatType, boolean isChest) {
		return isChest ? "textures/entity/chest_boat/" + boatType.getName() + ".png" : "textures/entity/boat/" + boatType.getName() + ".png";
	}
	
	/** Renders the desired {@code T} type Entity. */
	@Override
	public void render(ModBoat entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		matrixStackIn.pushPose();
		matrixStackIn.translate(0.0D, 0.375D, 0.0D);
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
		float f = (float) entityIn.getHurtTime() - partialTicks;
		float f1 = entityIn.getDamage() - partialTicks;
		if (f1 < 0.0F) {
			f1 = 0.0F;
		}

		if (f > 0.0F) {
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) entityIn.getHurtDir()));
		}

		float f2 = entityIn.getBubbleAngle(partialTicks);
		if (!Mth.equal(f2, 0.0F)) {
			matrixStackIn.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), entityIn.getBubbleAngle(partialTicks), true));
		}

		Pair<ResourceLocation, BoatModel> pair = getModelWithLocation(entityIn);
		ResourceLocation resourcelocation = pair.getFirst();
		BoatModel boatmodel = pair.getSecond();
		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		boatmodel.setupAnim(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexconsumer = bufferIn.getBuffer(boatmodel.renderType(resourcelocation));
		boatmodel.renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		if (!entityIn.isUnderWater()) {
			VertexConsumer vertexconsumer1 = bufferIn.getBuffer(RenderType.waterMask());
			boatmodel.waterPatch().render(matrixStackIn, vertexconsumer1, packedLightIn, OverlayTexture.NO_OVERLAY);
		}

		matrixStackIn.popPose();
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(ModBoat entity) {
		switch (entity.getCeilandsBoatType()) {
			case CEILTRUNK:
				return new ResourceLocation("ceilands:textures/entity/boat/ceiltrunk.png");
			case LUZAWOOD:
			default:
				return new ResourceLocation("ceilands:textures/entity/boat/luzawood.png");
		}
	}

	public Pair<ResourceLocation, BoatModel> getModelWithLocation(ModBoat boat) { return this.boatResources.get(boat.getCeilandsBoatType()); }
	
	private static ModelLayerLocation createLocation(String path, String p_171302_) {
		return new ModelLayerLocation(new ResourceLocation("minecraft", path), p_171302_);
		//return new ModelLayerLocation(new ResourceLocation("morecraft", p_171301_), p_171302_);
	}

	public static ModelLayerLocation createBoatModelName(ModBoat.Type p_171290_) {
		return createLocation("boat/oak", "main");
		//return createLocation("boat/" + p_171290_.getName(), "main");
	}

	public static ModelLayerLocation createChestBoatModelName(ModBoat.Type p_233551_) {
		return createLocation("chest_boat/oak", "main");
		//return createLocation("chest_boat/" + p_233551_.getName(), "main");
	}
}
