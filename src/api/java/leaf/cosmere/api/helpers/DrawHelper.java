/*
 * File updated ~ 2026-04-25 ~ Leaf (ported 1.20.1 Forge -> 1.21.1 NeoForge)
 */

package leaf.cosmere.api.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import leaf.cosmere.api.CosmereAPI;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.awt.Color;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DrawHelper
{

	//Draw our allomancy lines
	public static void drawLinesFromPoint(PoseStack poseStack, Vec3 originPoint, float range, Color color, List<Vec3> lineEndPositions, Vec3 highlightVector)
	{
		poseStack.pushPose();

		//move where the line is in the world. otherwise it is drawn around origin 0,0,0 I think?
		Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

		RenderSystem.disableDepthTest();
		RenderSystem.setShaderColor(1, 1, 1, 1);

		poseStack.translate(-view.x, -view.y, -view.z);

		//Tell the render system we're about to draw our lines
		//Use our line settings, special thanks to chisels and bits showing how that works.
		final MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		final VertexConsumer bufferIn = bufferSource.getBuffer(CosmereAPIRenderTypes.LINE_OVERLAY.get());

		//For all found things, draw the line
		for (Vec3 endPos : lineEndPositions)
		{
			ClientSubLevelAccess subLevel = SableCompanion.INSTANCE.getContainingClient(endPos);
			if (subLevel != null)
			{
				Pose3dc subPose = subLevel.renderPose();
				endPos = subPose.transformPosition(endPos);
			}


			Color finalColor = color;

			if (highlightVector != null)
			{
				if (endPos.equals(highlightVector))
				{
					finalColor = Color.decode("#66b2ff");
				}
			}

			int alpha = (int) Math.max(0, Math.floor((1 - (originPoint.distanceTo(endPos) / range)) * finalColor.getAlpha()));  // distance dims the lines until out of range
			PoseStack.Pose pose = poseStack.last();
			Matrix4f matrix = pose.pose();

			bufferIn.addVertex(matrix, (float) originPoint.x(), (float) originPoint.y(), (float) originPoint.z())
					.setColor(finalColor.getRed(), finalColor.getGreen(), finalColor.getBlue(), alpha)
					.setNormal(pose, 0, 1, 0);

			bufferIn.addVertex(matrix, (float) endPos.x(), (float) endPos.y(), (float) endPos.z())
					.setColor(finalColor.getRed(), finalColor.getGreen(), finalColor.getBlue(), alpha)
					.setNormal(pose, 0, 1, 0);
		}

		bufferSource.endBatch(CosmereAPIRenderTypes.LINE_OVERLAY.get());
		poseStack.popPose();
		RenderSystem.enableDepthTest();
	}

	public static void drawSquareAtPoint(PoseStack pStack, Color color, List<Vec3> squarePosList, Vec3 destinationVec)
	{
		pStack.pushPose();
		Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

		RenderSystem.disableDepthTest();
		RenderSystem.setShaderColor(1, 1, 1, 1);

		pStack.translate(-view.x, -view.y, -view.z);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		// set up texture and buffer
		final ResourceLocation icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/particle/note.png");
		final RenderType RENDER_TYPE = CosmereAPIRenderTypes.SQUARE_TEX_OVERLAY(icon);
		final MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		final VertexConsumer bufferIn = bufferSource.getBuffer(RENDER_TYPE);

		final float size = 0.3F;
		for (Vec3 pos : squarePosList)
		{
			Vec3 directionalVec = pos.subtract(destinationVec).normalize();

			PoseStack.Pose pose = pStack.last();
			Matrix4f matrix4f = pose.pose();

			double pitch = Math.asin(-directionalVec.y);
			double yaw = Math.atan2(directionalVec.x, directionalVec.z);

			Quaternionf rotQuat = Axis.YP.rotationDegrees((float) Math.toDegrees(yaw));
			rotQuat.mul(Axis.XP.rotationDegrees((float) Math.toDegrees(pitch) + 90));

			float[] vertices = {
					-size, 0, -size,
					-size, 0, size,
					size, 0, size,
					size, 0, -size
			};

			float[] textureCoords = {
					1.0F, 0.0F,
					1.0F, 1.0F,
					0.0F, 1.0F,
					0.0F, 0.0F,
			};

			for (int i = 0; i < vertices.length; i += 3)
			{
				float vertexX = vertices[i];
				float vertexY = vertices[i + 1];
				float vertexZ = vertices[i + 2];

				Vector3f rotQuatVec = new Vector3f(vertexX, vertexY, vertexZ);
				rotQuatVec.rotate(rotQuat);

				float finalX = (float) (rotQuatVec.x() + pos.x());
				float finalY = (float) (rotQuatVec.y() + pos.y());
				float finalZ = (float) (rotQuatVec.z() + pos.z());

				int textureUCoord = (int) textureCoords[i / 3 * 2];
				int textureVCoord = (int) textureCoords[i / 3 * 2 + 1];

				squareTexVertex(bufferIn, pose, matrix4f, 1, finalX, finalY, finalZ, textureUCoord, textureVCoord, color);
			}
		}

		bufferSource.endBatch(RENDER_TYPE);
		pStack.popPose();
		RenderSystem.enableDepthTest();
	}

	//copied from DragonFireballRenderer.java
	private static void squareTexVertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, Matrix4f matrix4f, int uv2, float pX, float pY, float pZ, int pU, int pV, Color color)
	{
		vertexConsumer.addVertex(matrix4f, pX, pY, pZ)
				.setUv((float) pU, (float) pV)
				.setColor(color.getRed(), color.getGreen(), color.getBlue(), 127)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(uv2)
				.setNormal(pose, 0.0F, 1.0F, 0.0F);
	}

	public static void drawBlocksAtPoint(PoseStack poseStack, Color color, List<ScannedBlock> blockList, float range, Vec3 highlightVector, ArrayList<BlockPos> targetedClusterBlockList)
	{
		poseStack.pushPose();

		//move where the line is in the world. otherwise it is drawn around origin 0,0,0 I think?
		Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

		RenderSystem.disableDepthTest();
		RenderSystem.setShaderColor(1, 1, 1, 1);

		poseStack.translate(-view.x, -view.y, -view.z);

		final VertexConsumer bufferIn = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(CosmereAPIRenderTypes.BLOCK_OVERLAY.get());

		for (ScannedBlock scannedBlock : blockList)
		{
			BlockPos blockPos = scannedBlock.pos();
			ClientSubLevelAccess subLevel = scannedBlock.subLevel();

			Color finalColor = color;
			if (highlightVector != null)
			{
				if (targetedClusterBlockList.contains(blockPos) || blockPos.getCenter().equals(highlightVector))
				{
					finalColor = Color.decode("#66b2ff");
				}
			}

			Vec3 worldCenter = subLevel != null
					? subLevel.renderPose().transformPosition(blockPos.getCenter())
					: blockPos.getCenter();
			float alphaPercent = (float) Math.max(0f, (1.0f - (SableCompanion.INSTANCE.rectilinearDistanceWithSubLevels(Minecraft.getInstance().level, view, worldCenter) / range)));

			renderColoredBlock(poseStack, bufferIn, finalColor, alphaPercent, blockPos, subLevel);
		}

		//we are meant to end batches... but if I don't, then the boxes draw over other boxes.
		Minecraft.getInstance().renderBuffers().bufferSource().endBatch(CosmereAPIRenderTypes.BLOCK_OVERLAY.get());
		poseStack.popPose();
		RenderSystem.enableDepthTest();
	}


	protected static void renderColoredBlock(PoseStack poseStack, VertexConsumer builder, Color color, float alphaPercent, BlockPos pos, ClientSubLevelAccess subLevel)
	{
		if (subLevel != null)
		{
			renderBoxSolidInSublevel(
					poseStack,
					builder,
					pos,
					color.getRed() / 255f,
					color.getGreen() / 255f,
					color.getBlue() / 255f,
					0.15f * alphaPercent,
					subLevel);
		} else
		{
			renderBoxSolid(
					poseStack,
					builder,
					pos,
					color.getRed() / 255f,
					color.getGreen() / 255f,
					color.getBlue() / 255f,
					0.15f * alphaPercent);
		}
	}

	protected static void renderBoxSolidInSublevel(PoseStack poseStack, VertexConsumer builder, BlockPos pos, float r, float g, float b, float alpha, ClientSubLevelAccess subLevel)
	{
		if (subLevel != null)
		{
			Pose3dc subPose = subLevel.renderPose();
			int bx = pos.getX(), by = pos.getY(), bz = pos.getZ();
			Vec3 c000 = subPose.transformPosition(new Vec3(bx, by, bz));
			Vec3 c100 = subPose.transformPosition(new Vec3(bx + 1, by, bz));
			Vec3 c101 = subPose.transformPosition(new Vec3(bx + 1, by,bz + 1));
			Vec3 c001 = subPose.transformPosition(new Vec3(bx, by,bz + 1));
			Vec3 c010 = subPose.transformPosition(new Vec3(bx,by+1, bz));
			Vec3 c110 = subPose.transformPosition(new Vec3(bx + 1,by + 1, bz));
			Vec3 c111 = subPose.transformPosition(new Vec3(bx + 1,by + 1,bz + 1));
			Vec3 c011 = subPose.transformPosition(new Vec3(bx,by + 1,bz + 1));

			PoseStack.Pose pose = poseStack.last();
			Matrix4f matrix = pose.pose();

			//down
			builder.addVertex(matrix, (float)c000.x, (float)c000.y, (float)c000.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c100.x, (float)c100.y, (float)c100.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c101.x, (float)c101.y, (float)c101.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c001.x, (float)c001.y, (float)c001.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);

			//up
			builder.addVertex(matrix, (float)c010.x, (float)c010.y, (float)c010.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c011.x, (float)c011.y, (float)c011.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c111.x, (float)c111.y, (float)c111.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c110.x, (float)c110.y, (float)c110.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);

			//east
			builder.addVertex(matrix, (float)c000.x, (float)c000.y, (float)c000.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c010.x, (float)c010.y, (float)c010.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c110.x, (float)c110.y, (float)c110.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c100.x, (float)c100.y, (float)c100.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);

			//west
			builder.addVertex(matrix, (float)c001.x, (float)c001.y, (float)c001.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c101.x, (float)c101.y, (float)c101.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c111.x, (float)c111.y, (float)c111.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c011.x, (float)c011.y, (float)c011.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);

			//south
			builder.addVertex(matrix, (float)c100.x, (float)c100.y, (float)c100.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c110.x, (float)c110.y, (float)c110.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c111.x, (float)c111.y, (float)c111.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c101.x, (float)c101.y, (float)c101.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);

			//north
			builder.addVertex(matrix, (float)c000.x, (float)c000.y, (float)c000.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c001.x, (float)c001.y, (float)c001.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c011.x, (float)c011.y, (float)c011.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
			builder.addVertex(matrix, (float)c010.x, (float)c010.y, (float)c010.z).setColor(r, g, b, alpha).setNormal(pose, 0, 0, 0);
		}
	}

	protected static void renderBoxSolid(PoseStack poseStack, VertexConsumer builder, BlockPos pos, float red, float green, float blue, float alpha) {
		float startX = pos.getX() - 0.001f;
		float startY = pos.getY() - 0.001f;
		float startZ = pos.getZ() - 0.001f;
		float endX = pos.getX() + 1.0015f;
		float endY = pos.getY() + 1.0015f;
		float endZ = pos.getZ() + 1.0015f;

		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix = pose.pose();

		//down
		builder.addVertex(matrix, startX, startY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, startY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, startY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, startX, startY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);

		//up
		builder.addVertex(matrix, startX, endY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, startX, endY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, endY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, endY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);

		//east
		builder.addVertex(matrix, startX, startY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, startX, endY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, endY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, startY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);

		//west
		builder.addVertex(matrix, startX, startY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, startY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, endY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, startX, endY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);

		//south
		builder.addVertex(matrix, endX, startY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, endY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, endY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, endX, startY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);

		//north
		builder.addVertex(matrix, startX, startY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, startX, startY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, startX, endY, endZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
		builder.addVertex(matrix, startX, endY, startZ).setColor(red, green, blue, alpha).setNormal(pose, 0, 0, 0);
	}

	//Special thanks to Chisels and Bits for showing how this works
	public enum CosmereAPIRenderTypes
	{
		LINE_OVERLAY(() -> Internal.LINE_OVERLAY),
		BLOCK_OVERLAY(() -> Internal.BLOCK_OVERLAY);

		private final Supplier<RenderType> typeSupplier;

		CosmereAPIRenderTypes(final Supplier<RenderType> typeSupplier)
		{
			this.typeSupplier = typeSupplier;
		}

		public RenderType get()
		{
			return typeSupplier.get();
		}

		public static RenderType SQUARE_TEX_OVERLAY(ResourceLocation icon)
		{
			return Internal.SQUARE_OVERLAY.apply(icon, true);
		}

		private static class Internal extends RenderType
		{
			private static final RenderType LINE_OVERLAY = RenderType.create(CosmereAPI.COSMERE_MODID + ":lines",
					DefaultVertexFormat.POSITION_COLOR_NORMAL,
					VertexFormat.Mode.LINES,
					256,
					false,
					false,
					CompositeState.builder()
							.setShaderState(RENDERTYPE_LINES_SHADER)
							.setLineState(new LineStateShard(OptionalDouble.of(2.5d)))
							.setLayeringState(VIEW_OFFSET_Z_LAYERING)
							.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
							.setOutputState(TRANSLUCENT_TARGET)
							.setWriteMaskState(COLOR_WRITE)
							.setCullState(NO_CULL)
							.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
							.createCompositeState(false));

			private static final RenderType BLOCK_OVERLAY = create(CosmereAPI.COSMERE_MODID + ":block_render",
					DefaultVertexFormat.POSITION_COLOR_NORMAL,
					VertexFormat.Mode.QUADS,
					25565,
					false,
					false,
					RenderType.CompositeState.builder()
							.setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
							.setTextureState(NO_TEXTURE)
							.setLayeringState(VIEW_OFFSET_Z_LAYERING)
							.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
							.setOutputState(TRANSLUCENT_TARGET)
							.setWriteMaskState(COLOR_WRITE)
							.setCullState(NO_CULL)
							.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
							.createCompositeState(false));

			private static final BiFunction<ResourceLocation, Boolean, RenderType> SQUARE_OVERLAY = Util.memoize((icon, createComposite) ->
					create(CosmereAPI.COSMERE_MODID + ":square_render",
							DefaultVertexFormat.POSITION_TEX,
							VertexFormat.Mode.QUADS,
							25565,
							false,
							false,
							RenderType.CompositeState.builder()
									.setShaderState(RenderStateShard.POSITION_TEX_SHADER)
									.setTextureState(new RenderStateShard.TextureStateShard(icon, false, false))
									.setLayeringState(VIEW_OFFSET_Z_LAYERING)
									.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
									.setOutputState(TRANSLUCENT_TARGET)
									.setWriteMaskState(COLOR_WRITE)
									.setCullState(NO_CULL)
									.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
									.createCompositeState(createComposite)));


			private Internal(String name, VertexFormat fmt, VertexFormat.Mode glMode, int size, boolean doCrumbling, boolean depthSorting, Runnable onEnable, Runnable onDisable)
			{
				super(name, fmt, glMode, size, doCrumbling, depthSorting, onEnable, onDisable);
				throw new IllegalStateException("This class must not be instantiated");
			}
		}
	}

}
