package thevoidkeeper.simpleforest.tree.species.oak.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import thevoidkeeper.simpleforest.tree.common.TreePlacementValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** An experimental reference-inspired oak silhouette for Minecraft 26.3.
 *  This replaces only the existing LargeOakTreeFeature, not small/medium oaks.
 *  Three-stage process: short trunk -> forked branch skeleton -> continuous foliage canopy.
 *  The generated plan stores block roles for a future tree-felling system, but it is not persisted yet.
 */
public final class LargeOakGenerator {
    private static final BlockState LOG = Blocks.OAK_LOG.defaultBlockState();
    private static final BlockState LEAF = Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);

    private enum Role { TRUNK, BRANCH, TWIG, LEAF, DECORATION }
    private record Part(BlockState state, Role role) {}
    private record CrownNode(BlockPos pos, int radius) {}

    private LargeOakGenerator() {}

    public static boolean generate(WorldGenLevel world, RandomSource random, BlockPos origin) {
        int height = 19 + random.nextInt(10);
        if (!siteSuitable(world, origin, height)) return false;
        Map<BlockPos, Part> plan = new HashMap<>();
        List<CrownNode> nodes = new ArrayList<>();

        // Reference silhouette: comparatively short clear bole, early spreading fork.
        int forkY = (int) Math.round(height * (0.38 + random.nextDouble() * 0.10));
        int x = 0, z = 0;
        double bendAngle = random.nextDouble() * Math.PI * 2.0;
        int bendLevel = 3 + random.nextInt(4);
        BlockPos center = origin;
        for (int y = 0; y <= forkY + 3; y++) {
            if (y > bendLevel && y % 5 == 0 && random.nextDouble() < 0.35) {
                x += (int) Math.round(Math.cos(bendAngle));
                z += (int) Math.round(Math.sin(bendAngle));
            }
            center = origin.offset(x, y, z);
            // Buttressed 3x3 base tapers to 2x2 early, then to a narrower fork.
            if (y < 2) {
                for (int dx=-1;dx<=1;dx++) for(int dz=-1;dz<=1;dz++) {
                    if (Math.abs(dx)==1 && Math.abs(dz)==1 && y==1) continue;
                    add(plan, center.offset(dx,0,dz), LOG, Role.TRUNK);
                }
            } else if (y < Math.max(4, forkY - 4)) {
                add(plan, center, LOG, Role.TRUNK);
                add(plan, center.west(), LOG, Role.TRUNK);
                add(plan, center.north(), LOG, Role.TRUNK);
                add(plan, center.north().west(), LOG, Role.TRUNK);
            } else {
                add(plan, center, LOG, Role.TRUNK);
                if (y < forkY) add(plan, center.west(), LOG, Role.TRUNK);
            }
        }
        BlockPos fork = center.below(3);
        int primary = 4 + random.nextInt(3); // 4..6 major forks
        double phase = random.nextDouble() * Math.PI * 2.0;
        for (int i=0; i<primary; i++) {
            double a = phase + 2.0 * Math.PI * i / primary + (random.nextDouble()-0.5)*0.55;
            double length = 7 + random.nextInt(5);
            int rise = 3 + random.nextInt(4);
            BlockPos tip = fork.offset((int)Math.round(Math.cos(a)*length), rise,
                    (int)Math.round(Math.sin(a)*length));
            drawBranch(plan, fork, tip, Role.BRANCH, true);
            nodes.add(new CrownNode(tip, 3 + random.nextInt(2)));
            // Child branches spread from intermediate points; some terminate lower for a broad crown.
            int children = 2 + random.nextInt(2);
            for (int c=0;c<children;c++) {
                double f = 0.52 + random.nextDouble()*0.35;
                BlockPos sideStart = interpolate(fork, tip, f);
                double sideAngle = a + (c%2==0 ? 1:-1)*(0.45+random.nextDouble()*0.45);
                int sideLength = 3+random.nextInt(4);
                BlockPos sideTip = sideStart.offset((int)Math.round(Math.cos(sideAngle)*sideLength),
                        random.nextInt(4)-1, (int)Math.round(Math.sin(sideAngle)*sideLength));
                drawBranch(plan, sideStart, sideTip, Role.BRANCH, false);
                nodes.add(new CrownNode(sideTip, 2+random.nextInt(2)));
            }
        }
        // Inner upper branches connect the crowns instead of producing a tall cone at the center.
        for (int i=0;i<3;i++) {
            double a=phase+(i+0.5)*Math.PI*2/3;
            BlockPos tip=fork.offset((int)Math.round(Math.cos(a)*(3+random.nextInt(3))),
                    5+random.nextInt(3), (int)Math.round(Math.sin(a)*(3+random.nextInt(3))));
            drawBranch(plan, fork, tip, Role.BRANCH, false);
            nodes.add(new CrownNode(tip, 3));
        }

        // Shared canopy volume: elliptical upper envelope with variable height and gaps.
        // It overlays branch-end foliage, so the crown appears connected and wide rather than in separate balls.
        int crownY = fork.getY()+5;
        int radius = 9+random.nextInt(3);
        for (int dx=-radius;dx<=radius;dx++) for(int dz=-radius;dz<=radius;dz++) {
            double r=Math.sqrt(dx*dx+dz*dz)/(double)radius;
            double az=Math.atan2(dz,dx);
            double waviness = 0.08*Math.sin(az*5+phase)+0.06*Math.cos(az*3-phase);
            if (r>1.0+waviness) continue;
            int roof=crownY+3+(int)Math.round(3.0*Math.sqrt(Math.max(0,1-r*r)))
                    +(int)Math.round(1.1*Math.sin(dx*0.55+dz*0.29+phase));
            int floor=crownY-3+(int)Math.round(2*r);
            for(int y=floor;y<=roof;y++) {
                double outer = Math.max(0.0, r-0.65);
                double chance=0.98-outer*0.8;
                if(y==roof || y==floor) chance-=0.15;
                // Repeated small holes between the major leaf volumes.
                if(random.nextDouble()<chance) addLeaf(plan, new BlockPos(fork.getX()+dx,y,fork.getZ()+dz));
            }
        }
        // More irregular edge clusters and a few draping undersides.
        for(CrownNode node:nodes) {
            addBlob(plan,node.pos,node.radius,2,random);
            if(random.nextInt(4)==0) {
                BlockPos drape=node.pos.below(2);
                addBlob(plan,drape,1,2,random);
            }
        }

        // Details use vanilla blocks, are intentionally sparse, and do not create floating carpets.
        List<Map.Entry<BlockPos,Part>> snapshot=new ArrayList<>(plan.entrySet());
        for(Map.Entry<BlockPos,Part> entry:snapshot) {
            BlockPos p=entry.getKey();
            if(entry.getValue().role==Role.LEAF && random.nextInt(210)==0
                    && !plan.containsKey(p.above())) {
                add(plan,p.above(),Blocks.MOSS_CARPET.defaultBlockState(),Role.DECORATION);
            }
        }

        // Reject a placement if ANY structural block collides. Never overwrite another tree or terrain.
        for(Map.Entry<BlockPos,Part> entry:plan.entrySet()) {
            Role role=entry.getValue().role;
            if(role==Role.TRUNK || role==Role.BRANCH || role==Role.TWIG) {
                BlockPos p=entry.getKey();
                if(!world.getBlockState(p).isAir() || !world.getFluidState(p).isEmpty()) return false;
            }
        }
        // Apply wood before leaves and decorations.
        for(Role role:Role.values()) for(Map.Entry<BlockPos,Part> entry:plan.entrySet()) {
            if(entry.getValue().role!=role) continue;
            BlockPos p=entry.getKey();
            if(world.getBlockState(p).isAir() && world.getFluidState(p).isEmpty())
                world.setBlock(p,entry.getValue().state,2);
        }
        return true;
    }

    private static void drawBranch(Map<BlockPos,Part> plan, BlockPos start, BlockPos end,
                                   Role role, boolean substantial) {
        int n=Math.max(1,Math.max(Math.abs(end.getX()-start.getX()),
                Math.max(Math.abs(end.getY()-start.getY()),Math.abs(end.getZ()-start.getZ()))));
        BlockPos previous=start;
        for(int i=0;i<=n;i++) {
            BlockPos p=interpolate(start,end,(double)i/n);
            // Join intermediate points orthogonally, preserving connected tree geometry.
            int cx=previous.getX(),cy=previous.getY(),cz=previous.getZ();
            while(cx!=p.getX()) { cx+=Integer.signum(p.getX()-cx); add(plan,new BlockPos(cx,cy,cz),LOG,role); }
            while(cz!=p.getZ()) { cz+=Integer.signum(p.getZ()-cz); add(plan,new BlockPos(cx,cy,cz),LOG,role); }
            while(cy!=p.getY()) { cy+=Integer.signum(p.getY()-cy); add(plan,new BlockPos(cx,cy,cz),LOG,role); }
            previous=p;
            add(plan,p,LOG,role);
            if(substantial && i<n/3) add(plan,p.below(),LOG,role);
        }
        // Thin side twigs using vanilla fences; no stair/slab at structural joints because their shape
        // may make a large dark patch and exposed plank textures. This can be upgraded later.
        if(!substantial && Math.abs(end.getX()-start.getX())+Math.abs(end.getZ()-start.getZ())>4) {
            BlockPos twig=end.above();
            add(plan,twig,Blocks.OAK_FENCE.defaultBlockState(),Role.TWIG);
        }
    }

    private static BlockPos interpolate(BlockPos a,BlockPos b,double t) {
        return new BlockPos((int)Math.round(a.getX()+(b.getX()-a.getX())*t),
                (int)Math.round(a.getY()+(b.getY()-a.getY())*t),
                (int)Math.round(a.getZ()+(b.getZ()-a.getZ())*t));
    }
    private static void addBlob(Map<BlockPos,Part> plan,BlockPos center,int radius,int halfHeight,RandomSource random) {
        for(int dx=-radius;dx<=radius;dx++) for(int dz=-radius;dz<=radius;dz++)
            for(int dy=-halfHeight;dy<=halfHeight;dy++) {
                double d=(dx*dx+dz*dz)/(double)(radius*radius)
                        +(dy*dy)/(double)(halfHeight*halfHeight);
                if(d<1.15+random.nextDouble()*0.15) addLeaf(plan,center.offset(dx,dy,dz));
            }
    }
    private static void addLeaf(Map<BlockPos,Part> plan, BlockPos p) {
        plan.putIfAbsent(p,new Part(LEAF,Role.LEAF));
    }
    private static void add(Map<BlockPos,Part> plan,BlockPos p,BlockState state,Role role) {
        // Structural wood overrides planned foliage, but decorations cannot override anything.
        if(role==Role.DECORATION) plan.putIfAbsent(p,new Part(state,role));
        else if(role==Role.LEAF) addLeaf(plan,p);
        else plan.put(p,new Part(state,role));
    }
    private static boolean siteSuitable(WorldGenLevel world,BlockPos origin,int height) {
        if(!TreePlacementValidator.validGround(world.getBlockState(origin.below()))) return false;
        int supported=0,min=Integer.MAX_VALUE,max=Integer.MIN_VALUE;
        for(int dx=-1;dx<=1;dx++) for(int dz=-1;dz<=1;dz++) {
            BlockPos base=origin.offset(dx,-1,dz);
            if(!world.getFluidState(base).isEmpty() || !world.getFluidState(base.above()).isEmpty()) return false;
            for(int dy=2;dy>=-2;dy--) {
                BlockPos test=base.offset(0,dy,0);
                if(TreePlacementValidator.validGround(world.getBlockState(test))) {
                    supported++;min=Math.min(min,test.getY());max=Math.max(max,test.getY());break;
                }
            }
        }
        if(supported<6 || max-min>2) return false;
        for(int y=0;y<height+4;y++) {
            BlockPos p=origin.above(y);
            if(!world.getBlockState(p).isAir() || !world.getFluidState(p).isEmpty()) return false;
        }
        return true;
    }
}
