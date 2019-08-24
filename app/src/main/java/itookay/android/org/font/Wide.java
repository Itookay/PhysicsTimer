package itookay.android.org.font;

public class Wide extends FontBase {

    private static final long serialVersionUID = 1L;

    public Wide() {
        NAME = "Wide";

        COLUMN_COUNT = 6;
        SEPARATE_COLUMN_COUNT = 1;
        ROW_COUNT = 7;
        ARRAY_SIZE = COLUMN_COUNT * ROW_COUNT;

        int[]	zero =
                {	0, 1, 1, 1, 1, 0,
                    1, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    0, 1, 1, 1, 1, 0 };
        ZERO = zero.clone();

        int[]	one =
                {	0, 0, 1, 0, 0, 0,
                    0, 0, 0, 1, 0, 0,
                    0, 0, 0, 1, 0, 0,
                    0, 0, 0, 1, 0, 0,
                    0, 0, 0, 1, 0, 0,
                    0, 0, 0, 1, 0, 0,
                    0, 0, 0, 1, 0, 0 };
        ONE = one.clone();

        int[]	two =
                {	0, 1, 1, 1, 1, 0,
                    1, 0, 0, 0, 0, 1,
                    0, 0, 0, 0, 0, 1,
                    0, 0, 0, 0, 1, 0,
                    0, 0, 0, 1, 0, 0,
                    0, 0, 1, 0, 0, 0,
                    1, 1, 1, 1, 1, 1 };
        TWO = two.clone();

        int[]	three=
                {	0, 1, 1, 1, 1, 0,
                    1, 0, 0, 0, 0, 1,
                    0, 0, 0, 0, 0, 1,
                    0, 0, 1, 1, 1, 0,
                    0, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    0, 1, 1, 1, 1, 0 };
        THREE = three.clone();

        int[]	four =
                {	0, 0, 1, 0, 0, 0,
                    0, 1, 0, 0, 0, 0,
                    1, 0, 0, 0, 1, 0,
                    1, 0, 0, 0, 1, 0,
                    1, 1, 1, 1, 1, 1,
                    0, 0, 0, 0, 1, 0,
                    0, 0, 0, 0, 1, 0 };
        FOUR = four.clone();

        int[]	five =
                {	0, 1, 1, 1, 1, 0,
                    1, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 0,
                    0, 1, 1, 1, 1, 0,
                    0, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    0, 1, 1, 1, 1, 0 };
        FIVE = five.clone();

        int[]	six =
                {	0, 1, 1, 1, 1, 0,
                    1, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 0,
                    1, 1, 1, 1, 1, 0,
                    1, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    0, 1, 1, 1, 1, 0 };
        SIX = six.clone();

        int[]	seven =
                {	1, 1, 1, 1, 1, 0,
                    0, 0, 0, 0, 0, 1,
                    0, 0, 0, 0, 0, 1,
                    0, 0, 0, 0, 1, 0,
                    0, 0, 0, 1, 0, 0,
                    0, 0, 1, 0, 0, 0,
                    0, 1, 0, 0, 0, 0 };
        SEVEN = seven.clone();

        int[]	eight =
                {	0, 1, 1, 1, 1, 0,
                    1, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    0, 1, 1, 1, 1, 0,
                    1, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    0, 1, 1, 1, 1, 0 };
        EIGHT = eight.clone();

        int[]	nine =
                {	0, 1, 1, 1, 1, 0,
                    1, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    0, 1, 1, 1, 1, 0,
                    0, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 1,
                    0, 1, 1, 1, 1, 0 };
        NINE = nine.clone();

        int[]	none =
                {	0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0 };
        NONE = none.clone();

        int[]	cologne =
                {	0,
                    0,
                    1,
                    0,
                    0,
                    1,
                    0,	};
        COLOGNE = cologne.clone();

        fontArray.add(0, ZERO);
        fontArray.add(1, ONE);
        fontArray.add(2, TWO);
        fontArray.add(3, THREE);
        fontArray.add(4, FOUR);
        fontArray.add(5, FIVE);
        fontArray.add(6, SIX);
        fontArray.add(7, SEVEN);
        fontArray.add(8, EIGHT);
        fontArray.add(9, NINE);
        fontArray.add(10, NONE);
    }
}
