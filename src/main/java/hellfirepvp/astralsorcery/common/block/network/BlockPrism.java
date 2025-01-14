/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalPropertyItem;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalPrismLens;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTest2
 * Created by HellFirePvP
 * Date: 07.08.2016 / 22:37
 */
public class BlockPrism extends BlockStarlightNetwork implements CrystalPropertyItem {

    private static final AxisAlignedBB boxPrismDown =  new AxisAlignedBB(3D/16D, 0,      3D/16D, 13D/16D, 14D/16D, 13D/16D);
    private static final AxisAlignedBB boxPrismUp =    new AxisAlignedBB(3D/16D, 2D/16D, 3D/16D, 13D/16D, 1,       13D/16D);
    private static final AxisAlignedBB boxPrismNorth = new AxisAlignedBB(3D/16D, 3D/16D, 0,      13D/16D, 13D/16D, 14D/16D);
    private static final AxisAlignedBB boxPrismSouth = new AxisAlignedBB(3D/16D, 3D/16D, 2D/16D, 13D/16D, 13D/16D, 1);
    private static final AxisAlignedBB boxPrismEast =  new AxisAlignedBB(2D/16D, 3D/16D, 3D/16D, 1,       13D/16D, 13D/16D);
    private static final AxisAlignedBB boxPrismWest =  new AxisAlignedBB(0,      3D/16D, 3D/16D, 14D/16D, 13D/16D, 13D/16D);

    public static PropertyEnum<EnumFacing> PLACED_AGAINST = PropertyEnum.create("against", EnumFacing.class);

    public BlockPrism() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(3.0F);
        setSoundType(SoundType.GLASS);
        setResistance(12.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(PLACED_AGAINST, EnumFacing.DOWN));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        ItemStack stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
        list.add(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        CrystalProperties.addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip, getMaxSize(stack));
    }

    @Override
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_CELESTIAL;
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return CrystalProperties.getCrystalProperties(stack);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(PLACED_AGAINST)) {
            case NORTH:
                return boxPrismNorth;
            case SOUTH:
                return boxPrismSouth;
            case WEST:
                return boxPrismWest;
            case EAST:
                return boxPrismEast;
            case UP:
                return boxPrismUp;
            default:
            case DOWN:
                return boxPrismDown;
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.UP;
        for (EnumFacing f : EnumFacing.values()) {
            if(f.ordinal() == meta) {
                facing = f;
                break;
            }
        }
        return getDefaultState().withProperty(PLACED_AGAINST, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PLACED_AGAINST).ordinal();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(PLACED_AGAINST, facing.getOpposite());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PLACED_AGAINST);
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) { }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = super.getPickBlock(getActualState(state, world, pos), target, world, pos, player);
        TileCrystalPrismLens lens = MiscUtils.getTileAt(world, pos, TileCrystalPrismLens.class, true);
        if(lens != null && lens.getCrystalProperties() != null) {
            CrystalProperties.applyCrystalProperties(stack, lens.getCrystalProperties());
        } else {
            CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
        }
        return stack;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        TileCrystalPrismLens lens = MiscUtils.getTileAt(worldIn, pos, TileCrystalPrismLens.class, true);
        if(lens != null && !worldIn.isRemote && !player.isCreative()) {
            ItemStack drop;
            if(lens.getLensColor() != null) {
                drop = lens.getLensColor().asStack();
                ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            }

            drop = new ItemStack(BlocksAS.lensPrism);
            if (lens.getCrystalProperties() != null) {
                CrystalProperties.applyCrystalProperties(drop, lens.getCrystalProperties());
            } else {
                CrystalProperties.applyCrystalProperties(drop, new CrystalProperties(1, 0, 0, 0, -1));
            }
            ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote && playerIn.isSneaking()) {
            TileCrystalPrismLens lens = MiscUtils.getTileAt(worldIn, pos, TileCrystalPrismLens.class, true);
            if(lens != null && lens.getLensColor() != null) {
                ItemStack drop = lens.getLensColor().asStack();
                ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                SoundHelper.playSoundAround(Sounds.clipSwitch, worldIn, pos, 0.8F, 1.5F);
                lens.setLensColor(null);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCrystalPrismLens();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileCrystalPrismLens te = MiscUtils.getTileAt(worldIn, pos, TileCrystalPrismLens.class, true);
        if(te == null) return;
        te.onPlace(CrystalProperties.getCrystalProperties(stack));
    }

}
