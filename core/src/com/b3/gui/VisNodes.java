package com.b3.gui;

import com.b3.search.Node;
import com.b3.search.SearchTicker;
import com.b3.search.util.SearchAlgorithm;
import com.b3.search.util.takeable.LinkedListT;
import com.b3.search.util.takeable.PriorityQueueT;
import com.b3.search.util.takeable.StackT;
import com.b3.search.util.takeable.Takeable;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.*;

/**
 * A table which will display the frontier and visited nodes
 * in a useful way to inspect what is happening internally with the algorithm.
 *
 * Created by lewis on 08/02/16.
 */
public class VisNodes extends Table {

    private ScrollPane fp, vp;
    private Table ft, vt;
    private float timer;

    /**
     * Provides a description of how the search algorithms work,
     * giving instructions of how the nodes are managed.
     *
     * todo should this be short / concise, or long / descriptive?
     */
    private final String description = "How it works:\n" +
            "Starting at the start node, its \n" +
            "neighbours (successors) are inspected.\n" +
            "These are inserted into the data \n" +
            "collection, which depends on the search.\n" +
            "This collection forms the frontier, \n" +
            "which we expand in the order defined\n" +
            "by the collection (e.g. a stack \n" +
            "being First In, First Out).\n" +
            "\n" +
            "With each expansion we mark the node \n" +
            "as visited (using a hash set),\n" +
            "to ensure we do not expand it again.";

    /**
     * Create a new data visualisation table
     *
     * @param stage The stage with which to render
     * @param skin The skin of the table and scrollpanes (background of scrollpane is removed)
     */
    public VisNodes(Stage stage, Skin skin) {
        super(skin);

        //anchor the table to the top-left position
        left().top();

        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
        style.background = getBackground();
        style.hScroll = skin.getDrawable("scroll_back_hor");
        style.hScrollKnob = skin.getDrawable("knob_05");
        style.vScroll = skin.getDrawable("scroll_back_ver");
        style.vScrollKnob = skin.getDrawable("knob_05");

        //frontier table, encapsulated in a scrollpane
        ft = new Table(getSkin());
        ft.top();
        fp = new ScrollPane(ft, style);
        fp.setFadeScrollBars(false);
        fp.setScrollingDisabled(true, false);

        //setup style of scrollpane - remove the grey background
        //from the style generated automatically
        // ScrollPane.ScrollPaneStyle style = fp.getStyle();
//        style.background = null;
//        fp.setStyle(style);

        //visited table, encapsulated in a scrollpane
        vt = new Table(getSkin());
        vt.top();
        vp = new ScrollPane(vt, style);
        vp.setFadeScrollBars(false);
        vp.setScrollingDisabled(true, false);

        stage.addActor(vp);
        stage.addActor(fp);

    }

    /**
     * Render the table, using a given search ticker
     * This method will handle null values appropriately to save this occurring outside the object
     *
     * @param ticker The search ticker whose values are to be rendered.
     */
    public void render(SearchTicker ticker) {
        int render;
        if(ticker==null || ticker.getVisited()==null || ticker.getFrontier()==null) {
            render =  render(new StackT<>(), new HashSet<>(), SearchAlgorithm.BREADTH_FIRST);
        } else {
            render =  render(ticker.getFrontier(), ticker.getVisited(), ticker.getAlgorithm());
        }
        if (ticker != null) {
        if(render==2) {
            ticker.pause(0);
        } else if(render == 1 && ticker.isPaused()) {
            ticker.resume(0);
        }
        }
    }

    /**
     * Render the table, currently using frame counter
     * Therefore will only render every 30th call
     *
     * @param front the frontier to display
     * @param visited the visited set to display
     * @param alg the algorithm currently being used by the search
     *
     * @return the state of the render  - 0: Not yet time to render
     *                                  - 1: Rendered as normal
     *                                  - 2: The scrollpane(s) are being dragged.
     */
    public int render(Collection<Node> front, Set<Node> visited, SearchAlgorithm alg) {
        //stop it rendering every frame
        float timeBetweenTicks = Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS);
        timer += Utils.TRUE_DELTA_TIME;
        if (timer < timeBetweenTicks)
            return 0;

        timer -= timeBetweenTicks;

        if(vp.isDragging() || vp.isFlinging() || vp.isPanning() || fp.isDragging() || fp.isFlinging() || fp.isPanning()) {
            return 2;
        }

        //check whether we need to render a data collection
        boolean rendermore = !front.isEmpty() || !visited.isEmpty();

        //setup the table
        setupTable(alg, rendermore);

        //if we need to render a data collection
        if(rendermore) {
            //get the frontier, as an arraylist
            ArrayList<Node> frontier = new ArrayList<>(front);
            //make the arraylist be ordered based on take-order of the collection
            //todo doesn't currently do correctly for A* - get(0) is still highest priority, however.
            switch(alg) {
                case DEPTH_FIRST: Collections.reverse(frontier);break;
            }

            ArrayList<Node> visitedSorted = new ArrayList<>(visited);
            Collections.sort(visitedSorted, (p1, p2) -> {
                if (p1.getPoint().getX() == p2.getPoint().getX()) {
                    return p1.getPoint().getY() - p2.getPoint().getY();
                }
                return p1.getPoint().getX() - p2.getPoint().getX();
            });


            //if(!vp.isDragging() && !fp.isDragging()) {
            for (int i = 0; i < Math.max(frontier.size(), visitedSorted.size()); i++) {
                vt.row();
                ft.row();
                if (frontier.size() > i) {
                    ft.add(frontier.get(i).toString());
                }
                if (visitedSorted.size() > i) {
                    vt.add(visitedSorted.get(i).toString());
                }
            }
           // }
        }
        return 1;
    }

    /**
     * Converts a data collection to a list,
     * which is of the same order as when taking from the collection
     *
     * todo currently doesn't work because it <b>removes</b> the elements from the collection
     *
     * @param front
     * @param alg
     * @return
     */
    @Deprecated
    private ArrayList<Node> colToList(Collection<Node> front, SearchAlgorithm alg) {
        ArrayList<Node> list = new ArrayList<>();
        //System.out.println(front);
        Takeable<Node> temp = null;
        //System.out.println(temp);

        switch(alg) {
            case DEPTH_FIRST: temp = new StackT<>(); break;
            case BREADTH_FIRST: temp = new LinkedListT<>(); break;
            case A_STAR: temp = new PriorityQueueT<>(null); break; // todo uh oh, I hope this stays deprecated -- it'll only be used when it's all working, atm the method doesn't work in the slightest
        }

//        for(int i=0; i<front.size(); i++) {
//            Node n = front.();
//            list.add(i,n);
//            temp.add(n);
//        }
        //System.out.println(front + "," + temp);
        front = temp;
        //System.out.println(front + "," + temp);
        return list;
    }

    /**
     * Reset (clear) the table,
     * Setting up the layout of the table in the process:
     * Adds the cells and rows required to make the table.
     * Nothing further needs be performed after calling this, add data to vt and ft
     *
     * @param alg The search algorithm being used by the search
     * @param rendermore Whether the data collections are being rendered
     */
    private void setupTable(SearchAlgorithm alg, boolean rendermore) {
        //clear the tables
        clear();
        ft.clear();
        vt.clear();

        //get what type the frontier is using
        String frontierDesc = "";
        switch(alg) {
            case DEPTH_FIRST: frontierDesc = "DFS: LIFO (stack)"; break;
            case BREADTH_FIRST: frontierDesc = "BFS: FIFO (queue)"; break;
            case A_STAR: frontierDesc = "A*: Priority Queue"; break;
        }

        //we need to render the data collections
        if(rendermore) {
            //row 1 - titles
            add("Frontier");
            add("   ");
            add("Visited nodes");
            row();

            //spacing
            add("");
            row();

            //row 2 - description of data collections
            add(frontierDesc);
            add("   ");
            add("Using Hash Set");
            row();
            //row 3 - note that highest frontier node is highest priority
            add("--Highest Priority--");
            add("   ");
            add("");
            row();

            //set up height to set for the scroll panes
            float h = Gdx.graphics.getHeight();
            float sh = h / 3;

            //row 4 - display the scroll panes holding the collection tables
            add(fp).fill().height(sh).maxHeight(sh);
            add("   ");
            add(vp).fill().height(sh).maxHeight(sh);
            row();

            //row 5 - note that lowest frontier node is lowest priority
            add("--Lowest Priority--");
            add("");
            row();
        } else {
            add("No search in progress...");
            row();

            add("");
            row();
        }

        //final row - describe the algorithm in words
        add(description).colspan(3);
    }

    /**
     * Get ninepatch from given file
     *
     * Source: http://www.mets-blog.com/libgdx-table-background/
     * Image source: http://www.mets-blog.com/wp-content/uploads/2015/11/ng.9.png
     * @return
     */
    private NinePatch getNinePatch() {
        String fname = "core/assets/ng.9.png";
        // Get the image
        final Texture t = new Texture(Gdx.files.internal(fname));

        // create a new texture region, otherwise black pixels will show up too, we are simply cropping the image
        // last 4 numbers respresent the length of how much each corner can draw,
        // for example if your image is 50px and you set the numbers 50, your whole image will be drawn in each corner
        // so what number should be good?, well a little less than half would be nice
        return new NinePatch( new TextureRegion(t, 1, 1 , t.getWidth() - 2, t.getHeight() - 2), 20, 20, 20, 20);

    }
}
