package com.example.asus.happispellcrossword.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.happispellcrossword.R;
import com.example.asus.happispellcrossword.activity.GameActivity;
import com.example.asus.happispellcrossword.model.WordObject;
import com.example.asus.happispellcrossword.model.WordObjectsManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by ThangDuong on 12-May-16.
 */
public class GridviewAdapter extends BaseAdapter {
    public static final String DISABLE = "0";
    public static final String ENABLE = "";
//    public static final int NORMAL = 2;
//    public static final int MAIN_CLICKED = 3;
//    public static final int SUB_CLICKED = 4;
//    public static final int TRANSPARENT = 5;
//    private static int color[][];//The board must be rectangular + data[x][y]
    private static boolean isAnimation[][];//The board must be rectangular + data[x][y]
//    private static WordObject clickedOject = null;
    ObjectAnimator scaleAnimationX = new ObjectAnimator();
    ObjectAnimator scaleAnimationY = new ObjectAnimator();
    WordObjectsManager objManager = WordObjectsManager.getInstance();
    private Context context;
    private ChangeLevelInterface changeLevelListener;
    private String data[][];//The board must be rectangular + data[x][y]
    private String answer[][];
    private int imageLocation[];
//    private ArrayList<WordObject> listWord = new ArrayList<WordObject>();
//    private OnItemGridViewClick gridViewClickListener;
    private Animation animation;
    private DisplayImageOptions opt;
    private ImageLoader imageLoader;
    private ArrayList<WordObject> listQuestion=new ArrayList<WordObject>();
    private static boolean isQuestionAnimation[];
    boolean isNotifyDoneLevel=false;

    public GridviewAdapter(Activity context, String[][] data) {
//        this.gridViewClickListener = (OnItemGridViewClick) context;
        isNotifyDoneLevel=false;
        this.context = context;
        this.data = data;
        this.changeLevelListener=(ChangeLevelInterface)context;
        setupAnswer();
        isQuestionAnimation=new boolean[listQuestion.size()];
        for(int i=0;i<listQuestion.size();i++)
            isQuestionAnimation[i]=true;
//        color = new int[data.length][data[0].length];
        isAnimation = new boolean[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++)//the board is rectangular
            {
                isAnimation[i][j] = false;
            }
        }

        int tempSize = objManager.getObjectArrayList().size();
        imageLocation = new int[tempSize];
        for(int i=0;i<tempSize;i++){
            WordObject wordObject = objManager.get(i);
            if(wordObject.getOrientation()==WordObject.HORIZONTAL){
                imageLocation[i]=wordObject.startX+wordObject.startY*10+wordObject.getResult().length();
            }else
                imageLocation[i]=wordObject.startX+(wordObject.startY+wordObject.getResult().length())*10;
            //NOT DISABLE image cell
            this.data[imageLocation[i]%GameActivity.NUM_OF_COLLUMN][imageLocation[i]/GameActivity.NUM_OF_ROW] = ENABLE;
        }
        initImageLoader(context);
        setupImageDisplayOptions();
//        resetColor();
//        onClickCell(objManager.get(0).startX, objManager.get(0).startY);
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(6 * 1024 * 1024); // 6 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        // Initialize ImageLoader with configuration.
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config.build());
    }

    private void setupImageDisplayOptions() {
        opt = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_error_48pt)
                .showImageOnFail(R.drawable.ic_error_48pt)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }


    private void setupAnswer() {
        answer=new String[GameActivity.NUM_OF_COLLUMN][GameActivity.NUM_OF_COLLUMN];
        this.listQuestion=objManager.getObjectArrayList();
        for(WordObject wordObject:listQuestion){
            String answerQuestion=wordObject.getResult();
            int startX=wordObject.startX;
            int startY=wordObject.startY;
            if(wordObject.getOrientation()==WordObject.HORIZONTAL){
                for(int i=0;i<answerQuestion.length();i++)
                    answer[startX+i][startY]=answerQuestion.substring(i,i+1);
            }else {
                for(int i=0;i<answerQuestion.length();i++)
                    answer[startX][startY+i]=answerQuestion.substring(i,i+1);
            }
        }
    }

    @Override
    public int getCount() {
        return data.length * data[0].length;//The board must be rectangular
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // LayoutInflater to call external grid_item.xml file

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View gridView;
        if (convertView == null) {
            // get layout from grid_item.xml ( Defined Below )
            gridView = inflater.inflate(R.layout.grid_puzzle_item, null);
        } else {
            gridView = convertView;
        }

        final Button cell = (Button) gridView.findViewById(R.id.button);
        final TextView textViewNumberQuestion = (TextView) gridView.findViewById(R.id.tvItemSTT);

//        //set Row Height
//        DisplayMetrics metrics = new DisplayMetrics();
//        metrics = context.getResources().getDisplayMetrics();
//        cell.setMinimumHeight(0);
////        cell.setHeight(MainActivity.getRowHeight());
//        cell.setHeight((int) ((metrics.widthPixels / GameActivity.NUM_OF_ROW) * 0.9));

        cell.setTextColor(Color.BLACK);
        final int positionX = position % GameActivity.NUM_OF_COLLUMN;
        final int positionY = position / GameActivity.NUM_OF_ROW;
        if(data[positionX][positionY]==GridviewAdapter.DISABLE){
            cell.setVisibility(View.INVISIBLE);
            cell.setFocusable(false);
            cell.setEnabled(false);
            cell.setClickable(false);
        }else {
            if(data[positionX][positionY].equals(answer[positionX][positionY])) {
                boolean checkAnswer=true;
                cell.setText(data[positionX][positionY]);
                for (int j=0;j<listQuestion.size();j++) {
                    WordObject wordObject=listQuestion.get(j);
                    String answerQuestion = wordObject.getResult();
                    int startX = wordObject.startX;
                    int startY = wordObject.startY;
                    if (wordObject.startY == positionY&&wordObject.getOrientation()==WordObject.HORIZONTAL) {
                        for (int i = 0; i < answerQuestion.length(); i++) {
                             if (!data[startX + i][startY].equals(answerQuestion.substring(i, i + 1))) {
                                checkAnswer = false;
                            }
                        }
                        if(checkAnswer&&positionX>=startX&&positionX<=(startX+answerQuestion.length())&&isQuestionAnimation[j]){
                            Animation animationScale = AnimationUtils.loadAnimation(context, R.anim.character_scale);
                            gridView.startAnimation(animationScale);
                            if(positionX==startX+answerQuestion.length()-1)
                                isQuestionAnimation[j]=false;
                            if(checkResult()&&!isNotifyDoneLevel){
                                //changeLevelListener.increaseLevel();
                                changeLevelListener.notifyDoneQuestion();
                                isNotifyDoneLevel=true;
                            }
                        }
                    }
                    checkAnswer=true;
                    if(wordObject.startX==positionX&&wordObject.getOrientation()==WordObject.VERTICAL){
                        for (int i = 0; i < answerQuestion.length(); i++) {
                            if (!data[startX][startY+i].equals(answerQuestion.substring(i, i + 1))) {
                                checkAnswer = false;
                            }
                        }
                        if(checkAnswer&&positionY>=startY&&positionY<=(startY+answerQuestion.length())&&isQuestionAnimation[j]){
                            Animation animationScale = AnimationUtils.loadAnimation(context, R.anim.character_scale);
                            gridView.startAnimation(animationScale);
                            if(positionY==startY+answerQuestion.length()-1)
                                isQuestionAnimation[j]=false;
                            if(checkResult()&&!isNotifyDoneLevel){
                                //changeLevelListener.increaseLevel();
                                changeLevelListener.notifyDoneQuestion();
                                isNotifyDoneLevel=true;
                            }
                        }
                    }
                    checkAnswer=true;
                }
            }
        }

        //Setup Image cells
        for(int i=0;i<imageLocation.length;i++)
        {
            int imgLocation=imageLocation[i];
            if(position==imgLocation)//if this is the image cell
            {
                //cell.setBackgroundResource(R.drawable.ic_action_word);
                imageLoader.loadImage(listQuestion.get(i).getImageLink(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        cell.setBackground(new BitmapDrawable(loadedImage));
                    }
                });
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation animationScale = AnimationUtils.loadAnimation(context, R.anim.scale);
                        gridView.startAnimation(animationScale);
                    }
                });
            }
        }
        return gridView;
    }

    private void settingAnimation() {
        scaleAnimationX.setStartDelay(0);
        scaleAnimationX.setRepeatCount(0);
        scaleAnimationX.setRepeatMode(ValueAnimator.REVERSE);
        scaleAnimationX.setInterpolator(new LinearInterpolator());
        scaleAnimationY.setStartDelay(0);
        scaleAnimationY.setRepeatCount(0);
        scaleAnimationY.setRepeatMode(ValueAnimator.REVERSE);
        scaleAnimationY.setInterpolator(new LinearInterpolator());
    }

    private void startScaleAnimation(View view, int duration) {
        scaleAnimationX = ObjectAnimator.ofFloat(view, "scaleX", new float[]{0.5f, 1.0f}).setDuration(duration);
        scaleAnimationY = ObjectAnimator.ofFloat(view, "scaleY", new float[]{0.5f, 1.0f}).setDuration(duration);
        final AnimatorSet animation = new AnimatorSet();
        animation.playTogether(scaleAnimationX, scaleAnimationY);
        animation.start();
    }

    private boolean checkResult() {
        boolean checkAnswer = true;
        for (WordObject question : listQuestion) {
            String answer = "";
            int firstX = question.startX;
            int firstY = question.startY;
            if (question.getOrientation() == WordObject.HORIZONTAL) {
                for (int i = 0; i < question.getResult().length(); i++) {
                    answer = answer.concat(data[firstX + i][firstY]);
                }
                if (!answer.equals(question.getResult()))
                    checkAnswer = false;
            } else {
                for (int i = 0; i < question.getResult().length(); i++) {
                    answer = answer.concat(data[firstX][firstY + i]);
                }
                if (!answer.equals(question.getResult()))
                    checkAnswer = false;
            }

        }
        return checkAnswer;
    }

    public interface ChangeLevelInterface{
        public void increaseLevel();
        public void notifyDoneQuestion();
    }

    private void onClickCell(int x, int y) {
        //Reset color of the grid
//        resetColor();

        /*WordObject obj = objManager.getObjectAt(x, y);
        if (obj != null)// If clicked inside a word
        {
            obj.setClickedPosition(x, y);
            clickedOject = obj;
//            colorSurroundCells(x, y);
        }*/
    }

    /*public interface OnItemGridViewClick {
        void onItemGridViewClick(int position);
    }*/

    /*public void setUpListWord(ArrayList<WordObject> listWord) {
        this.listWord = listWord;
    }*/

    /*public void resetColor() {
        for (int i = 0; i < color.length; i++) {
            for (int j = 0; j < color[0].length; j++)//the board is rectangular
            {
                WordObject obj = objManager.getObjectAt(i, j);
                if (obj != null)
                    color[i][j] = ENABLE;
                else
                    color[i][j] = DISABLE;
            }
        }
        notifyDataSetChanged();
    }*/

    /*private void colorSurroundCells(int x, int y)// x,y is the starting point, cell is the work obj to check its length
    {
        color[x][y] = MAIN_CLICKED;
        int tempX = x + 1;// color the subclicked on the right
        while (tempX < data.length && clickedOject.isInsideWord(tempX, y)) {
            color[tempX][y] = SUB_CLICKED;
            tempX++;
        }
        tempX = x - 1;// color the subclicked on the right
        while (tempX >= 0 && clickedOject.isInsideWord(tempX, y)) {
            color[tempX][y] = SUB_CLICKED;
            tempX--;
        }

        int tempY = y + 1;// color the subclicked downward
        while (tempY < data[0].length && clickedOject.isInsideWord(x, tempY)) {
            color[x][tempY] = SUB_CLICKED;
            tempY++;
        }
        tempY = y - 1;// color the subclicked upward
        while (tempY >= 0 && clickedOject.isInsideWord(x, tempY)) {
            color[x][tempY] = SUB_CLICKED;
            tempY--;
        }
        notifyDataSetChanged();
    }*/

    /*public void nextClickedPosition() {
        int lastClickedX = WordObject.getClickedPositionX();
        int lastClickedY = WordObject.getClickedPositionY();
        //Horizontal and stop at last digit
        if (clickedOject.getOrientation() == WordObject.HORIZONTAL
                && WordObject.getClickedPositionX() <= clickedOject.startX + clickedOject.getResult().length() - 2) {
            if (lastClickedX < clickedOject.startX + clickedOject.getResult().length())
                lastClickedX++;
        }

        //Vertical and stop at last digit
        if (clickedOject.getOrientation() == WordObject.VERTICAL
                && WordObject.getClickedPositionY() <= clickedOject.startY + clickedOject.getResult().length() - 2) {
            if (lastClickedY < clickedOject.startY + clickedOject.getResult().length())
                lastClickedY++;
        }
        clickedOject.setClickedPosition(lastClickedX, lastClickedY);

//        onClickCell(WordObject.getClickedPositionX(), WordObject.getClickedPositionY());

        colorSurroundCells(lastClickedX, lastClickedY);
    }*/

    /*public void backClickedPosition() {
        int lastClickedX = WordObject.getClickedPositionX();
        int lastClickedY = WordObject.getClickedPositionY();
        //Horizontal and stop at last digit
        if (clickedOject.getOrientation() == WordObject.HORIZONTAL
                && WordObject.getClickedPositionX() < clickedOject.startX + clickedOject.getResult().length()) {
            if (lastClickedX > clickedOject.startX)
                lastClickedX--;
        }

        //Vertical and stop at last digit
        if (clickedOject.getOrientation() == WordObject.VERTICAL
                && WordObject.getClickedPositionY() < clickedOject.startY + clickedOject.getResult().length()) {
            if (lastClickedY > clickedOject.startY)
                lastClickedY--;
        }
        clickedOject.setClickedPosition(lastClickedX, lastClickedY);

//        onClickCell(WordObject.getClickedPositionX(), WordObject.getClickedPositionY());

        colorSurroundCells(lastClickedX, lastClickedY);
    }*/
}
