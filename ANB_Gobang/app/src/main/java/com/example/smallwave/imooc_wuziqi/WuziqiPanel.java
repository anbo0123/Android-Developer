package com.example.smallwave.imooc_wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WuziqiPanel extends View {

    // 声明成员变量
    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;
    private int MAX_COUNT_IN_LINE = 5;

    // 画笔对象
    private Paint mPaint = new Paint();

    // 引入棋子图片
    private Bitmap mWhitePiece; // 白棋
    private Bitmap mBlackPiece; // 黑棋

    // 比例
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    // 白棋先手，当前轮到白棋
    private boolean mIsWhite = true;
    // 白棋 黑棋 存储坐标
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    // 成员变量
    private boolean mIsGameOver;    // 判断游戏是否结束
    private boolean mIsWhiteWinner; // 判断赢家

    // 构造方法
    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 设置透明的颜色
//        setBackgroundColor(0x44ff0000);
        // 初始化
        init();
    }

    private void init() {
        // 初始化 画笔
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        // 初始化 棋子图片
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }

    // 手势方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) return false; // 表明游戏结束

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point p = getValidPoint(x, y);
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }

            if (mIsWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite = !mIsWhite;
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    // 初始化测量的方法
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 拿到Size  Mode的值
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 【正方形】取值为最小值  当widthSize 或 heightSize为0时，View是无法显示的，此时需要做一个判断
        int width = Math.min(widthSize, heightSize);

        // 判断性 设置宽度
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        // 设置 宽高值
        setMeasuredDimension(width, width);
    }

    // 当你的宽高确定发生改变后，会回调
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        // 声明一个目标宽度
        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        // 修改图片尺寸
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    // View的绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制棋盘
        drawBoard(canvas);

        // 绘制棋子
        drawPieces(canvas);

        // 检测游戏进行
        checkGameOver();
    }

    private void checkGameOver() {
        // 判断游戏结束，是否有连续的5子
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);

        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;

            String text = mIsWhiteWinner ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    // 判断是否有连续的5个棋子
    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;

            boolean win = checkHorizontal(x, y, points);
            if (win)return  true;
            win = checkVertical(x, y, points);
            if (win)return  true;
            win = checkLeftDiagonal(x, y, points);
            if (win)return  true;
            win = checkRightDiagonal(x, y, points);
            if (win)return  true;
        }

        return false;
    }

    // 判断x，y位置的棋子，是否横向相邻的5个一致
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;  // 记录棋子个数
        // 横向左边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        // 判断count
        if (count == MAX_COUNT_IN_LINE) return true;
        // 横向右边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        // 判断count
        if (count == MAX_COUNT_IN_LINE) return true;

        return false;
    }
    // 判断x，y位置的棋子，是否纵向相邻的5个一致
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;  // 记录棋子个数
        // 上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        // 判断count
        if (count == MAX_COUNT_IN_LINE) return true;
        // 下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        // 判断count
        if (count == MAX_COUNT_IN_LINE) return true;

        return false;
    }
    // 判断x，y位置的棋子，是否左斜相邻的5个一致
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;  // 记录棋子个数
        // 左上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        // 判断count
        if (count == MAX_COUNT_IN_LINE) return true;
        // 左下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        // 判断count
        if (count == MAX_COUNT_IN_LINE) return true;

        return false;
    }
    // 判断x，y位置的棋子，是否右斜相邻的5个一致
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;  // 记录棋子个数
        // 右上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        // 判断count
        if (count == MAX_COUNT_IN_LINE) return true;
        // 右下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        // 判断count
        if (count == MAX_COUNT_IN_LINE) return true;

        return false;
    }


    // 绘制棋子
    private void drawPieces(Canvas canvas) {
        // 绘制白棋
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    null);
        }
        // 绘制黑棋
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    null);
        }
    }

    // 绘制棋盘
    private void drawBoard(Canvas canvas) {

        int w = mPanelWidth;
        float lineHeight = mLineHeight;

//        for (int i = 0; i < MAX_LINE; i++){
//
//            int startX = (int) (lineHeight / 2);
//            int endX = (int) (w - lineHeight / 2);
//
//            int y = (int) ((0.5 + i) * lineHeight);
//            // 横线
//            canvas.drawLine(startX, y, endX, y, mPaint);
//            // 纵线
//            canvas.drawLine(y, startX, y, endX,mPaint);
//        }

        // 横线
        for (int i = 0; i < MAX_LINE; i++) {

            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);

            canvas.drawLine(startX, y, endX, y, mPaint);
        }
        // 纵线
        for (int i = 0; i < MAX_LINE; i++) {

            int startY = (int) (lineHeight / 2);
            int endY = (int) (w - lineHeight / 2);

            int x = (int) ((0.5 + i) * lineHeight);

            canvas.drawLine(x, startY, x, endY, mPaint);
        }
    }

    /*
     *  需要存储的值：白子，黑子的集合；游戏进行的状态（是否结束）。
     *  注意：当存储与恢复的逻辑正确处理完成后，还是没有达到想要的效果。此时应该检查布局文件中，是否给自定义的View指定了id，如果没有指定id是不能存储与恢复的。
     */
    // 定义常量的Key
    private static final String INSTANCE = "instance"; // 存储默认的（必须存储）
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    // View的存储
    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    // View的恢复
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // 判断state是否为我们定义的Bundle类型
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            // 注意取值后，赋值
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    // 再来一局
    public void start(){
        mWhiteArray.clear();;
        mBlackArray.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();
    }
}
