package net.dollar.simplegear.block.entity;

import net.dollar.simplegear.ModMain;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

public class ModSpectralLanternBlockEntity extends BlockEntity implements UseBlockCallback, UseEntityCallback {
    private Instant lastUsedInstant = Instant.MIN;

    public ModSpectralLanternBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPECTRAL_LANTERN_BLOCK_ENTITY, pos, state);
    }

    @Override
    public BlockState getCachedState() {
        return super.getCachedState();
    }

    /**
     * Called when a player uses a Compound Gemstone on a Spectral Lantern. This method will verify that it is
     *  called server-side only, the Spectral Lantern was not used less than 15 seconds ago, and that there is
     *  valid space underneath the tile. If these conditions are met, select one boss from the pool of bosses,
     *  set its data, and consume the user's Compound Gemstone.
     * @param context The ItemUsageContext generated by the player using the Compound Gemstone on this tile entity
     * @return Whether the spawn was successful
     */
    @Deprecated
    public boolean attemptSpawnBoss(ItemUsageContext context) {
        //Only perform this logic server-side.
        if (!context.getWorld().isClient() && context.getPlayer() != null) {  //check for null to silence warnings
            //Fail if last attempt was less than 15 seconds ago.
            if (Duration.between(lastUsedInstant, Instant.now()).getSeconds() < 15) {
                context.getPlayer().sendMessage(Text.translatable("spectral_lantern.not_ready_yet"));
                return false;
            }

            //Require that 2 blocks below is air.
            context.getWorld().isAir(context.getBlockPos().down(2));
            if (!context.getWorld().isAir(context.getBlockPos().down(2))) {
                context.getPlayer().sendMessage(Text.translatable("spectral_lantern.area_not_clear"));
                return false;
            }

            HostileEntity entity = randomlySelectBoss(context);
            entity.setInvisible(true);
            entity.setSilent(true);

            entity.setPosition(context.getBlockPos().down(2).toCenterPos());    //move down 2
            context.getWorld().spawnEntity(entity);

            //If player not in creative, set target.
            if (!context.getPlayer().isCreative()) {
                entity.setTarget(context.getPlayer());
            }

            //Store timestamp for cooldown use.
            lastUsedInstant = Instant.now();

            //Consume one Compound Gemstone (guaranteed to still be Compound Gemstone in interactionHand).
            context.getStack().decrement(1);

            return true;
        }
        return false;
    }

    /**
     * Select a random boss from the pool of bosses, spawn it, send a system message, and return the new Monster Entity.
     * @param context The UseOnContext generated from the player using the Compound Gemstone
     * @return The spawned boss mob (Monster)
     */
    @Deprecated
    private HostileEntity randomlySelectBoss(ItemUsageContext context) {
        switch (RandomUtils.nextInt(0, 3)) {
            default -> {    //can interpret default as case 0, otherwise complains about duplicate
                //SEND MESSAGE IN CHAT TO NEARBY PLAYERS
                context.getPlayer().sendMessage(Text.translatable("spectral_lantern.kathleen_the_wicked"));
                //return new KathleenTheWickedEntity(ModEntities.KATHLEEN_THE_WICKED.get(), context.getWorld());
                return new SpiderEntity(EntityType.SPIDER, context.getWorld()); //TEMP
            }
            case 1 -> {
                context.getPlayer().sendMessage(Text.translatable("spectral_lantern.old_lady_muff"));
                //return new OldLadyMuffEntity(ModEntities.OLD_LADY_MUFF.get(), context.getWorld());
                return new SpiderEntity(EntityType.SPIDER, context.getWorld()); //TEMP
            }
            case 2 -> {
                context.getPlayer().sendMessage(Text.translatable("spectral_lantern.the_helirooster"));
                //return new TheHeliroosterEntity(ModEntities.THE_HELIROOSTER.get(), context.getWorld());
                return new SpiderEntity(EntityType.SPIDER, context.getWorld()); //TEMP
            }
        }
    }

    /**
     * Callback for when a player interacts with a block.
     * @param player Player interacting with this block
     * @param world Active world
     * @param hand Hand of the player interacting with this block
     * @param hitResult Generated BlockHitResult
     * @return The resulting ActionResult produced by the interaction
     */
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        //TODO: ENSURE THIS WORKS FOR BLOCK ENTITIES (DEFINITELY WORKS FOR BLOCKS)
        doLanternInteraction(player, world, hitResult.getPos());
        return ActionResult.CONSUME;
    }

    /**
     * Callback for when a player interacts with an Entity.
     * @param player Player interacting with this Entity
     * @param world Active world
     * @param hand Hand of the player interacting with this block
     * @param entity The interacted Entity???
     * @param hitResult Generated EntityHitResult
     * @return The resulting ActionResult produced by the interaction
     */
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        //TODO: ENSURE THIS WORKS FOR BLOCK ENTITIES (WORKS FOR OTHER ENTITIES)
        assert hitResult != null;   //Assert non-null to silence warning
        doLanternInteraction(player, world, hitResult.getPos());
        return ActionResult.CONSUME;
    }

    private void doLanternInteraction(PlayerEntity player, World world, Vec3d pos) {
        //TODO: IMPLEMENT BUFF NEARBY PLAYERS OPERATION AND SEND SYSTEM MESSAGE
        //HAVE THE LANTERN GET ANGRY AFTER A NUMBER OF REPEATED USES, SPAWNING A BOSS AND TARGETING THE USER
        //PUTS ITSELF ON COOLDOWN
        ModMain.LOGGER.info("PLAYER INTERACT WITH SPECTRAL LANTERN ENTITY");
    }
}