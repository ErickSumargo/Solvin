package id.solvin.dev.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.view.widget.CustomTypefaceSpan;

import java.util.Calendar;

/**
 * Created by Erick Sumargo on 1/6/2017.
 */

public class FragmentSolvinKeyboard extends Fragment {
    //    UI COMPONENT
    private View view;
    private GridLayout layout_menu, keyboardSolvin_low, keyboardSolvin_upp,
            keyboardSolvin_sym1, keyboardSolvin_sym2, keyboardSolvin_sym3;

    public ImageButton button_keyboard_mode, button_subscript, button_superscript,
            button_camera, button_upload, button_send;

    private Button key_zero_low, key_one_low, key_two_low, key_three_low, key_four_low,
            key_five_low, key_six_low, key_seven_low, key_eight_low, key_nine_low,
            key_a, key_b, key_c, key_d, key_e, key_f, key_g, key_h, key_i, key_j,
            key_k, key_l, key_m, key_n, key_o, key_p, key_q, key_r, key_s, key_t,
            key_u, key_v, key_w, key_x, key_y, key_z;
    private ImageButton key_upp, key_backspace_low, key_symbol_low, key_space_low, key_enter_low;

    private Button key_zero_upp, key_one_upp, key_two_upp, key_three_upp, key_four_upp,
            key_five_upp, key_six_upp, key_seven_upp, key_eight_upp, key_nine_upp,
            key_A, key_B, key_C, key_D, key_E, key_F, key_G, key_H, key_I, key_J,
            key_K, key_L, key_M, key_N, key_O, key_P, key_Q, key_R, key_S, key_T,
            key_U, key_V, key_W, key_X, key_Y, key_Z;
    private ImageButton key_low, key_backspace_upp, key_symbol_upp, key_space_upp, key_enter_upp;

    private Button key_zero_sym1, key_one_sym1, key_two_sym1, key_three_sym1, key_four_sym1,
            key_five_sym1, key_six_sym1, key_seven_sym1, key_eight_sym1, key_nine_sym1,
            key_plus, key_subtract, key_multiply1, key_division, key_equality, key_inequality,
            key_approximation, key_sqrt, key_percentage, key_permille, key_plus_minus, key_identical,
            key_multiply2, key_fraction, key_less, key_greater, key_less_equality, key_greater_equality,
            key_period, key_comma, key_colon, key_left_bracket, key_right_bracket, key_left_curly_bracket,
            key_right_curly_bracket, key_left_square_bracket, key_right_square_bracket,
            key_symbol_sym1, key_variable_sym1;
    private ImageButton key_backspace_sym1, key_space_sym1, key_enter_sym1;

    private Button key_zero_sym2, key_one_sym2, key_two_sym2, key_three_sym2, key_four_sym2,
            key_five_sym2, key_six_sym2, key_seven_sym2, key_eight_sym2, key_nine_sym2,
            key_sum, key_product, key_left_floor, key_right_floor, key_left_ceiling, key_right_ceiling,
            key_absolute, key_intersection, key_union, key_empty_set, key_element_of, key_not_element_of,
            key_subset_of, key_not_subset_of, key_triangle, key_nabla, key_congruent, key_similarity, key_angle,
            key_degree, key_universal, key_existential, key_negation, key_conjunction, key_disjunction,
            key_implication, key_biimplication, key_symbol_sym2, key_variable_sym2;
    private ImageButton key_backspace_sym2, key_space_sym2, key_enter_sym2;

    private Button key_zero_sym3, key_one_sym3, key_two_sym3, key_three_sym3, key_four_sym3,
            key_five_sym3, key_six_sym3, key_seven_sym3, key_eight_sym3, key_nine_sym3,
            key_xor, key_therefore, key_perpendicular, key_parallel, key_factorial, key_dot, key_integral, key_derivative,
            key_alpha, key_beta, key_gamma, key_delta, key_epsilon, key_theta, key_mu, key_pi, key_rho,
            key_sigma, key_tau, key_phi1, key_phi2, key_omega, key_eta, key_lamda, key_ohm, key_armstrong, key_infinity,
            key_symbol_sym3, key_variable_sym3;
    private ImageButton key_backspace_sym3, key_space_sym3, key_enter_sym3;

    private Toast toast;

    //    UI HELPER
    private RelativeLayout.LayoutParams menuParams;
    private ViewGroup.LayoutParams keyParams;
    private WindowManager windowManager;
    private Point point;
    private InputMethodManager inputMethodManager;
    private AudioManager audioManager;
    private CountDownTimer timer;
    private Typeface customFont, specialFont;
    private Bundle properties;

    //    LOCAL OBJECT
    private ClassApplicationTool applicationTool;

    //    LOCAL VARIABLE
    private int width, height, extraMargin, range;
    private final int CAPTURE_IMAGE = 0, PICK_IMAGE = 1;

    public boolean showAndroidKeyboard, showSolvinKeyboard, solvinKeyboardActived;
    private boolean subScript_mode, superScript_mode;
    private boolean longClickActive, longPressActive;

    private SpannableStringBuilder spannableCharacter;

    private ImageButton keyboardMenu[];
    private Button key_lower[], key_upper[], key_sym1[], key_sym2[], key_sym3[];
    ImageButton specialKey_low[], specialKey_upp[], specialKey_sym1[], specialKey_sym2[], specialKey_sym3[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(id.solvin.dev.R.layout.activity_solvin_keyboard, container, false);
        init();

        return view;
    }


    public FragmentSolvinKeyboard() {
    }

    private void init() {
        windowManager = getActivity().getWindowManager();
        point = new Point();

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/SourceSansPro-Regular.otf");
        specialFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Calibri.ttf");

        applicationTool = new ClassApplicationTool(getContext());
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        range = 1;

        showAndroidKeyboard = true;
        showSolvinKeyboard = solvinKeyboardActived = false;
        subScript_mode = superScript_mode = false;
        longClickActive = longPressActive = false;

        layout_menu = (GridLayout) view.findViewById(id.solvin.dev.R.id.layout_menu);
        keyboardSolvin_low = (GridLayout) view.findViewById(id.solvin.dev.R.id.layout_keyboardSolvin_low);
        keyboardSolvin_upp = (GridLayout) view.findViewById(id.solvin.dev.R.id.layout_keyboardSolvin_upp);
        keyboardSolvin_sym1 = (GridLayout) view.findViewById(id.solvin.dev.R.id.layout_keyboardSolvin_sym1);
        keyboardSolvin_sym2 = (GridLayout) view.findViewById(id.solvin.dev.R.id.layout_keyboardSolvin_sym2);
        keyboardSolvin_sym3 = (GridLayout) view.findViewById(id.solvin.dev.R.id.layout_keyboardSolvin_sym3);

        button_keyboard_mode = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_keyboard_mode);
        button_subscript = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_subscript);
        button_superscript = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_superscript);
        button_camera = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_camera);
        button_upload = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_upload);
        button_send = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_send);

        key_zero_low = (Button) view.findViewById(id.solvin.dev.R.id.button_zero_low);
        key_one_low = (Button) view.findViewById(id.solvin.dev.R.id.button_one_low);
        key_two_low = (Button) view.findViewById(id.solvin.dev.R.id.button_two_low);
        key_three_low = (Button) view.findViewById(id.solvin.dev.R.id.button_three_low);
        key_four_low = (Button) view.findViewById(id.solvin.dev.R.id.button_four_low);
        key_five_low = (Button) view.findViewById(id.solvin.dev.R.id.button_five_low);
        key_six_low = (Button) view.findViewById(id.solvin.dev.R.id.button_six_low);
        key_seven_low = (Button) view.findViewById(id.solvin.dev.R.id.button_seven_low);
        key_eight_low = (Button) view.findViewById(id.solvin.dev.R.id.button_eight_low);
        key_nine_low = (Button) view.findViewById(id.solvin.dev.R.id.button_nine_low);

        key_zero_upp = (Button) view.findViewById(id.solvin.dev.R.id.button_zero_upp);
        key_one_upp = (Button) view.findViewById(id.solvin.dev.R.id.button_one_upp);
        key_two_upp = (Button) view.findViewById(id.solvin.dev.R.id.button_two_upp);
        key_three_upp = (Button) view.findViewById(id.solvin.dev.R.id.button_three_upp);
        key_four_upp = (Button) view.findViewById(id.solvin.dev.R.id.button_four_upp);
        key_five_upp = (Button) view.findViewById(id.solvin.dev.R.id.button_five_upp);
        key_six_upp = (Button) view.findViewById(id.solvin.dev.R.id.button_six_upp);
        key_seven_upp = (Button) view.findViewById(id.solvin.dev.R.id.button_seven_upp);
        key_eight_upp = (Button) view.findViewById(id.solvin.dev.R.id.button_eight_upp);
        key_nine_upp = (Button) view.findViewById(id.solvin.dev.R.id.button_nine_upp);

        key_zero_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_zero_sym1);
        key_one_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_one_sym1);
        key_two_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_two_sym1);
        key_three_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_three_sym1);
        key_four_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_four_sym1);
        key_five_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_five_sym1);
        key_six_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_six_sym1);
        key_seven_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_seven_sym1);
        key_eight_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_eight_sym1);
        key_nine_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_nine_sym1);

        key_zero_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_zero_sym2);
        key_one_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_one_sym2);
        key_two_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_two_sym2);
        key_three_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_three_sym2);
        key_four_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_four_sym2);
        key_five_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_five_sym2);
        key_six_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_six_sym2);
        key_seven_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_seven_sym2);
        key_eight_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_eight_sym2);
        key_nine_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_nine_sym2);

        key_zero_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_zero_sym3);
        key_one_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_one_sym3);
        key_two_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_two_sym3);
        key_three_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_three_sym3);
        key_four_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_four_sym3);
        key_five_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_five_sym3);
        key_six_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_six_sym3);
        key_seven_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_seven_sym3);
        key_eight_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_eight_sym3);
        key_nine_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_nine_sym3);

        key_a = (Button) view.findViewById(id.solvin.dev.R.id.button_a);
        key_b = (Button) view.findViewById(id.solvin.dev.R.id.button_b);
        key_c = (Button) view.findViewById(id.solvin.dev.R.id.button_c);
        key_d = (Button) view.findViewById(id.solvin.dev.R.id.button_d);
        key_e = (Button) view.findViewById(id.solvin.dev.R.id.button_e);
        key_f = (Button) view.findViewById(id.solvin.dev.R.id.button_f);
        key_g = (Button) view.findViewById(id.solvin.dev.R.id.button_g);
        key_h = (Button) view.findViewById(id.solvin.dev.R.id.button_h);
        key_i = (Button) view.findViewById(id.solvin.dev.R.id.button_i);
        key_j = (Button) view.findViewById(id.solvin.dev.R.id.button_j);
        key_k = (Button) view.findViewById(id.solvin.dev.R.id.button_k);
        key_l = (Button) view.findViewById(id.solvin.dev.R.id.button_l);
        key_m = (Button) view.findViewById(id.solvin.dev.R.id.button_m);
        key_n = (Button) view.findViewById(id.solvin.dev.R.id.button_n);
        key_o = (Button) view.findViewById(id.solvin.dev.R.id.button_o);
        key_p = (Button) view.findViewById(id.solvin.dev.R.id.button_p);
        key_q = (Button) view.findViewById(id.solvin.dev.R.id.button_q);
        key_r = (Button) view.findViewById(id.solvin.dev.R.id.button_r);
        key_s = (Button) view.findViewById(id.solvin.dev.R.id.button_s);
        key_t = (Button) view.findViewById(id.solvin.dev.R.id.button_t);
        key_u = (Button) view.findViewById(id.solvin.dev.R.id.button_u);
        key_v = (Button) view.findViewById(id.solvin.dev.R.id.button_v);
        key_w = (Button) view.findViewById(id.solvin.dev.R.id.button_w);
        key_x = (Button) view.findViewById(id.solvin.dev.R.id.button_x);
        key_y = (Button) view.findViewById(id.solvin.dev.R.id.button_y);
        key_z = (Button) view.findViewById(id.solvin.dev.R.id.button_z);

        key_A = (Button) view.findViewById(id.solvin.dev.R.id.button_A);
        key_B = (Button) view.findViewById(id.solvin.dev.R.id.button_B);
        key_C = (Button) view.findViewById(id.solvin.dev.R.id.button_C);
        key_D = (Button) view.findViewById(id.solvin.dev.R.id.button_D);
        key_E = (Button) view.findViewById(id.solvin.dev.R.id.button_E);
        key_F = (Button) view.findViewById(id.solvin.dev.R.id.button_F);
        key_G = (Button) view.findViewById(id.solvin.dev.R.id.button_G);
        key_H = (Button) view.findViewById(id.solvin.dev.R.id.button_H);
        key_I = (Button) view.findViewById(id.solvin.dev.R.id.button_I);
        key_J = (Button) view.findViewById(id.solvin.dev.R.id.button_J);
        key_K = (Button) view.findViewById(id.solvin.dev.R.id.button_K);
        key_L = (Button) view.findViewById(id.solvin.dev.R.id.button_L);
        key_M = (Button) view.findViewById(id.solvin.dev.R.id.button_M);
        key_N = (Button) view.findViewById(id.solvin.dev.R.id.button_N);
        key_O = (Button) view.findViewById(id.solvin.dev.R.id.button_O);
        key_P = (Button) view.findViewById(id.solvin.dev.R.id.button_P);
        key_Q = (Button) view.findViewById(id.solvin.dev.R.id.button_Q);
        key_R = (Button) view.findViewById(id.solvin.dev.R.id.button_R);
        key_S = (Button) view.findViewById(id.solvin.dev.R.id.button_S);
        key_T = (Button) view.findViewById(id.solvin.dev.R.id.button_T);
        key_U = (Button) view.findViewById(id.solvin.dev.R.id.button_U);
        key_V = (Button) view.findViewById(id.solvin.dev.R.id.button_V);
        key_W = (Button) view.findViewById(id.solvin.dev.R.id.button_W);
        key_X = (Button) view.findViewById(id.solvin.dev.R.id.button_X);
        key_Y = (Button) view.findViewById(id.solvin.dev.R.id.button_Y);
        key_Z = (Button) view.findViewById(id.solvin.dev.R.id.button_Z);

        key_plus = (Button) view.findViewById(id.solvin.dev.R.id.button_plus);
        key_subtract = (Button) view.findViewById(id.solvin.dev.R.id.button_subtract);
        key_multiply1 = (Button) view.findViewById(id.solvin.dev.R.id.button_multiply1);
        key_division = (Button) view.findViewById(id.solvin.dev.R.id.button_division);
        key_equality = (Button) view.findViewById(id.solvin.dev.R.id.button_equality);
        key_inequality = (Button) view.findViewById(id.solvin.dev.R.id.button_inequality);
        key_sqrt = (Button) view.findViewById(id.solvin.dev.R.id.button_sqrt);
        key_percentage = (Button) view.findViewById(id.solvin.dev.R.id.button_percentage);
        key_permille = (Button) view.findViewById(id.solvin.dev.R.id.button_permille);
        key_plus_minus = (Button) view.findViewById(id.solvin.dev.R.id.button_plus_minus);
        key_multiply2 = (Button) view.findViewById(id.solvin.dev.R.id.button_multiply2);
        key_fraction = (Button) view.findViewById(id.solvin.dev.R.id.button_fraction);
        key_approximation = (Button) view.findViewById(id.solvin.dev.R.id.button_approximation);
        key_identical = (Button) view.findViewById(id.solvin.dev.R.id.button_identical);
        key_less = (Button) view.findViewById(id.solvin.dev.R.id.button_less);
        key_greater = (Button) view.findViewById(id.solvin.dev.R.id.button_greater);
        key_less_equality = (Button) view.findViewById(id.solvin.dev.R.id.button_less_equality);
        key_greater_equality = (Button) view.findViewById(id.solvin.dev.R.id.button_greater_equality);
        key_period = (Button) view.findViewById(id.solvin.dev.R.id.button_period);
        key_comma = (Button) view.findViewById(id.solvin.dev.R.id.button_comma);
        key_colon = (Button) view.findViewById(id.solvin.dev.R.id.button_colon);
        key_left_bracket = (Button) view.findViewById(id.solvin.dev.R.id.button_left_bracket);
        key_right_bracket = (Button) view.findViewById(id.solvin.dev.R.id.button_right_bracket);
        key_left_curly_bracket = (Button) view.findViewById(id.solvin.dev.R.id.button_left_curly_bracket);
        key_right_curly_bracket = (Button) view.findViewById(id.solvin.dev.R.id.button_right_curly_bracket);
        key_left_square_bracket = (Button) view.findViewById(id.solvin.dev.R.id.button_left_square_bracket);
        key_right_square_bracket = (Button) view.findViewById(id.solvin.dev.R.id.button_right_square_bracket);

        key_sum = (Button) view.findViewById(id.solvin.dev.R.id.button_sum);
        key_product = (Button) view.findViewById(id.solvin.dev.R.id.button_product);
        key_left_floor = (Button) view.findViewById(id.solvin.dev.R.id.button_left_floor);
        key_right_floor = (Button) view.findViewById(id.solvin.dev.R.id.button_right_floor);
        key_left_ceiling = (Button) view.findViewById(id.solvin.dev.R.id.button_left_ceiling);
        key_right_ceiling = (Button) view.findViewById(id.solvin.dev.R.id.button_right_ceiling);
        key_absolute = (Button) view.findViewById(id.solvin.dev.R.id.button_absolute);
        key_intersection = (Button) view.findViewById(id.solvin.dev.R.id.button_intersection);
        key_union = (Button) view.findViewById(id.solvin.dev.R.id.button_union);
        key_empty_set = (Button) view.findViewById(id.solvin.dev.R.id.button_empty_set);
        key_element_of = (Button) view.findViewById(id.solvin.dev.R.id.button_element_of);
        key_not_element_of = (Button) view.findViewById(id.solvin.dev.R.id.button_not_element_of);
        key_subset_of = (Button) view.findViewById(id.solvin.dev.R.id.button_subset_of);
        key_not_subset_of = (Button) view.findViewById(id.solvin.dev.R.id.button_not_subset_of);
        key_triangle = (Button) view.findViewById(id.solvin.dev.R.id.button_triangle);
        key_nabla = (Button) view.findViewById(id.solvin.dev.R.id.button_nabla);
        key_congruent = (Button) view.findViewById(id.solvin.dev.R.id.button_congruent);
        key_similarity = (Button) view.findViewById(id.solvin.dev.R.id.button_similarity);
        key_angle = (Button) view.findViewById(id.solvin.dev.R.id.button_angle);
        key_degree = (Button) view.findViewById(id.solvin.dev.R.id.button_degree);
        key_universal = (Button) view.findViewById(id.solvin.dev.R.id.button_universal);
        key_existential = (Button) view.findViewById(id.solvin.dev.R.id.button_existential);
        key_negation = (Button) view.findViewById(id.solvin.dev.R.id.button_negation);
        key_conjunction = (Button) view.findViewById(id.solvin.dev.R.id.button_conjunction);
        key_disjunction = (Button) view.findViewById(id.solvin.dev.R.id.button_disjunction);
        key_implication = (Button) view.findViewById(id.solvin.dev.R.id.button_implication);
        key_biimplication = (Button) view.findViewById(id.solvin.dev.R.id.button_biimplication);

        key_xor = (Button) view.findViewById(id.solvin.dev.R.id.button_xor);
        key_therefore = (Button) view.findViewById(id.solvin.dev.R.id.button_therefore);
        key_perpendicular = (Button) view.findViewById(id.solvin.dev.R.id.button_perpendicular);
        key_parallel = (Button) view.findViewById(id.solvin.dev.R.id.button_parallel);
        key_factorial = (Button) view.findViewById(id.solvin.dev.R.id.button_factorial);
        key_dot = (Button) view.findViewById(id.solvin.dev.R.id.button_dot);
        key_integral = (Button) view.findViewById(id.solvin.dev.R.id.button_integral);
        key_derivative = (Button) view.findViewById(id.solvin.dev.R.id.button_derivative);
        key_alpha = (Button) view.findViewById(id.solvin.dev.R.id.button_alpha);
        key_beta = (Button) view.findViewById(id.solvin.dev.R.id.button_beta);
        key_gamma = (Button) view.findViewById(id.solvin.dev.R.id.button_gamma);
        key_delta = (Button) view.findViewById(id.solvin.dev.R.id.button_delta);
        key_epsilon = (Button) view.findViewById(id.solvin.dev.R.id.button_epsilon);
        key_theta = (Button) view.findViewById(id.solvin.dev.R.id.button_theta);
        key_mu = (Button) view.findViewById(id.solvin.dev.R.id.button_mu);
        key_pi = (Button) view.findViewById(id.solvin.dev.R.id.button_pi);
        key_rho = (Button) view.findViewById(id.solvin.dev.R.id.button_rho);
        key_sigma = (Button) view.findViewById(id.solvin.dev.R.id.button_sigma);
        key_tau = (Button) view.findViewById(id.solvin.dev.R.id.button_tau);
        key_phi1 = (Button) view.findViewById(id.solvin.dev.R.id.button_phi1);
        key_phi2 = (Button) view.findViewById(id.solvin.dev.R.id.button_phi2);
        key_omega = (Button) view.findViewById(id.solvin.dev.R.id.button_omega);
        key_eta = (Button) view.findViewById(id.solvin.dev.R.id.button_eta);
        key_lamda = (Button) view.findViewById(id.solvin.dev.R.id.button_lamda);
        key_similarity = (Button) view.findViewById(id.solvin.dev.R.id.button_similarity);
        key_ohm = (Button) view.findViewById(id.solvin.dev.R.id.button_ohm);
        key_armstrong = (Button) view.findViewById(id.solvin.dev.R.id.button_armstrong);
        key_infinity = (Button) view.findViewById(id.solvin.dev.R.id.button_infinity);

        key_upp = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_upp);
        key_low = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_low);
        key_symbol_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_symbol_sym1);
        key_symbol_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_symbol_sym2);
        key_symbol_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_symbol_sym3);

        key_backspace_upp = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_backspace_upp);
        key_backspace_low = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_backspace_low);
        key_backspace_sym1 = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_backspace_sym1);
        key_backspace_sym2 = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_backspace_sym2);
        key_backspace_sym3 = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_backspace_sym3);

        key_symbol_upp = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_symbol_upp);
        key_symbol_low = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_symbol_low);
        key_variable_sym1 = (Button) view.findViewById(id.solvin.dev.R.id.button_variable_sym1);
        key_variable_sym2 = (Button) view.findViewById(id.solvin.dev.R.id.button_variable_sym2);
        key_variable_sym3 = (Button) view.findViewById(id.solvin.dev.R.id.button_variable_sym3);

        key_space_upp = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_space_upp);
        key_space_low = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_space_low);
        key_space_sym1 = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_space_sym1);
        key_space_sym2 = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_space_sym2);
        key_space_sym3 = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_space_sym3);

        key_enter_upp = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_enter_upp);
        key_enter_low = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_enter_low);
        key_enter_sym1 = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_enter_sym1);
        key_enter_sym2 = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_enter_sym2);
        key_enter_sym3 = (ImageButton) view.findViewById(id.solvin.dev.R.id.button_enter_sym3);

        key_lower = new Button[]{key_zero_low, key_one_low, key_two_low, key_three_low, key_four_low,
                key_five_low, key_six_low, key_seven_low, key_eight_low, key_nine_low,
                key_a, key_b, key_c, key_d, key_e, key_f, key_g, key_h, key_i, key_j,
                key_k, key_l, key_m, key_n, key_o, key_p, key_q, key_r, key_s, key_t,
                key_u, key_v, key_w, key_x, key_y, key_z};

        key_upper = new Button[]{key_zero_upp, key_one_upp, key_two_upp, key_three_upp, key_four_upp,
                key_five_upp, key_six_upp, key_seven_upp, key_eight_upp, key_nine_upp,
                key_A, key_B, key_C, key_D, key_E, key_F, key_G, key_H, key_I, key_J,
                key_K, key_L, key_M, key_N, key_O, key_P, key_Q, key_R, key_S, key_T,
                key_U, key_V, key_W, key_X, key_Y, key_Z};

        key_sym1 = new Button[]{key_zero_sym1, key_one_sym1, key_two_sym1, key_three_sym1, key_four_sym1,
                key_five_sym1, key_six_sym1, key_seven_sym1, key_eight_sym1, key_nine_sym1, key_plus,
                key_subtract, key_multiply1, key_division, key_equality, key_inequality, key_sqrt,
                key_percentage, key_permille, key_plus_minus, key_multiply2, key_fraction, key_approximation,
                key_identical, key_less, key_greater, key_less_equality, key_greater_equality, key_period,
                key_comma, key_colon, key_left_bracket, key_right_bracket, key_left_curly_bracket,
                key_right_curly_bracket, key_left_square_bracket, key_right_square_bracket,
                key_symbol_sym1, key_variable_sym1};

        key_sym2 = new Button[]{key_zero_sym2, key_one_sym2, key_two_sym2, key_three_sym2, key_four_sym2,
                key_five_sym2, key_six_sym2, key_seven_sym2, key_eight_sym2, key_nine_sym2, key_sum,
                key_product, key_left_floor, key_right_floor, key_left_ceiling, key_right_ceiling,
                key_absolute, key_intersection, key_union, key_empty_set, key_element_of, key_not_element_of,
                key_subset_of, key_not_subset_of, key_triangle, key_nabla, key_congruent, key_similarity, key_angle,
                key_degree, key_universal, key_existential, key_negation, key_conjunction, key_disjunction,
                key_implication, key_biimplication, key_symbol_sym2, key_variable_sym2};

        key_sym3 = new Button[]{key_zero_sym3, key_one_sym3, key_two_sym3, key_three_sym3, key_four_sym3,
                key_five_sym3, key_six_sym3, key_seven_sym3, key_eight_sym3, key_nine_sym3,
                key_xor, key_therefore, key_perpendicular, key_parallel, key_factorial, key_dot, key_integral, key_derivative,
                key_alpha, key_beta, key_gamma, key_delta, key_epsilon, key_theta, key_mu, key_pi, key_rho,
                key_sigma, key_tau, key_phi1, key_phi2, key_omega, key_eta, key_lamda, key_ohm, key_armstrong,
                key_infinity, key_symbol_sym3, key_variable_sym3};

        keyboardMenu = new ImageButton[]{button_keyboard_mode, button_subscript, button_superscript,
                button_camera, button_upload, button_send};

        specialKey_low = new ImageButton[]{key_upp, key_backspace_low, key_symbol_low, key_space_low, key_enter_low};
        specialKey_upp = new ImageButton[]{key_low, key_backspace_upp, key_symbol_upp, key_space_upp, key_enter_upp};
        specialKey_sym1 = new ImageButton[]{key_backspace_sym1, key_space_sym1, key_enter_sym1};
        specialKey_sym2 = new ImageButton[]{key_backspace_sym2, key_space_sym2, key_enter_sym2};
        specialKey_sym3 = new ImageButton[]{key_backspace_sym3, key_space_sym3, key_enter_sym3};

        windowManager.getDefaultDisplay().getSize(point);
        width = point.x;
        height = point.y;

        keyboardSolvin_low.animate().translationY(keyboardSolvin_low.getHeight()).alpha(0.0f);

        customizeKeyboardMenu(keyboardMenu);

        customizeKeyText(key_lower, 0);
        customizeKeyText(key_upper, 0);
        customizeKeyText(key_sym1, 1);
        customizeKeyText(key_sym2, 1);
        customizeKeyText(key_sym3, 1);

        customizeKeyImage(specialKey_low, 0);
        customizeKeyImage(specialKey_upp, 0);
        customizeKeyImage(specialKey_sym1, 1);
        customizeKeyImage(specialKey_sym2, 1);
        customizeKeyImage(specialKey_sym3, 1);

        extraMargin = addExtraMargin(7);
        properties = getArguments();
        if (properties != null) {
            button_send.setEnabled(properties.getBoolean("enabled"));
            button_send.setAlpha(properties.getFloat("alpha"));
        }

        key_backspace_low.setOnTouchListener(deleteTouchListener);
        key_backspace_upp.setOnTouchListener(deleteTouchListener);
        key_backspace_sym1.setOnTouchListener(deleteTouchListener);
        key_backspace_sym2.setOnTouchListener(deleteTouchListener);
        key_backspace_sym3.setOnTouchListener(deleteTouchListener);
    }

    public int addExtraMargin(int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, getResources().getDisplayMetrics());
    }

    public void touchSheetLayout() {
        if (!showAndroidKeyboard) {
            adjustView(height / 12 + 5 * height / 14 + extraMargin);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            menuParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            menuParams.addRule(RelativeLayout.ABOVE, id.solvin.dev.R.id.layout_keyboardSolvin_low);
            layout_menu.setLayoutParams(menuParams);

            if (keyboardSolvin_low.isShown()) {
                setKeyboardVisibility(true);
            } else {
                if (keyboardSolvin_upp.isShown()) {
                    keyboardSolvin_low.animate().translationY(0).alpha(1.0f);
                    keyboardSolvin_upp.animate().translationY(0).alpha(1.0f);
                } else if (keyboardSolvin_sym1.isShown()) {
                    keyboardSolvin_low.animate().translationY(0).alpha(1.0f);
                    keyboardSolvin_sym1.animate().translationY(0).alpha(1.0f);
                } else if (keyboardSolvin_sym2.isShown()) {
                    keyboardSolvin_low.animate().translationY(0).alpha(1.0f);
                    keyboardSolvin_sym2.animate().translationY(0).alpha(1.0f);
                } else if (keyboardSolvin_sym3.isShown()) {
                    keyboardSolvin_low.animate().translationY(0).alpha(1.0f);
                    keyboardSolvin_sym3.animate().translationY(0).alpha(1.0f);
                } else {
                    setKeyboardVisibility(true);
                }
            }
            showSolvinKeyboard = true;
        } else if (inputMethodManager == null) {
            adjustView(height / 12);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            setKeyboardVisibility(false);
        }
    }

    private void setMode() {
        if (showAndroidKeyboard) {
            toast = Toast.makeText(getActivity(), "Aktivasi Keyboard Solvin", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            button_keyboard_mode.setImageResource(id.solvin.dev.R.drawable.ic_solvin_primary);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    adjustView(height / 12 + 5 * height / 14 + extraMargin);

                    menuParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    menuParams.addRule(RelativeLayout.ABOVE, id.solvin.dev.R.id.layout_keyboardSolvin_low);
                    layout_menu.setLayoutParams(menuParams);
                }
            }, 200);

            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            setKeyboardVisibility(true);

            showAndroidKeyboard = false;
            showSolvinKeyboard = true;

            button_subscript.setEnabled(true);
            button_subscript.setAlpha(1f);

            button_superscript.setEnabled(true);
            button_superscript.setAlpha(1f);
        } else {
            adjustView(height / 12);

            button_keyboard_mode.setImageResource(id.solvin.dev.R.drawable.ic_solvin_dark);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menuParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    menuParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    layout_menu.setLayoutParams(menuParams);

                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                }
            }, 200);

            setKeyboardVisibility(false);

            showAndroidKeyboard = true;
            showSolvinKeyboard = false;

            button_subscript.setEnabled(false);
            button_subscript.setAlpha(0.25f);

            button_superscript.setEnabled(false);
            button_superscript.setAlpha(0.25f);
        }
    }

    private void setKeyboardVisibility(boolean visible) {
        if (visible) {
            keyboardSolvin_low.setVisibility(View.VISIBLE);
            keyboardSolvin_low.animate().translationY(0).alpha(1.0f);

            keyboardSolvin_upp.setVisibility(View.INVISIBLE);
            keyboardSolvin_upp.animate().translationY(0).alpha(1.0f);

            keyboardSolvin_sym1.setVisibility(View.INVISIBLE);
            keyboardSolvin_sym1.animate().translationY(0).alpha(1.0f);

            keyboardSolvin_sym2.setVisibility(View.INVISIBLE);
            keyboardSolvin_sym2.animate().translationY(0).alpha(1.0f);

            keyboardSolvin_sym3.setVisibility(View.INVISIBLE);
            keyboardSolvin_sym3.animate().translationY(0).alpha(1.0f);
        } else {
            keyboardSolvin_low.animate().translationY(keyboardSolvin_low.getHeight()).alpha(0.0f);
            keyboardSolvin_upp.animate().translationY(keyboardSolvin_upp.getHeight()).alpha(0.0f);
            keyboardSolvin_sym1.animate().translationY(keyboardSolvin_sym1.getHeight()).alpha(0.0f);
            keyboardSolvin_sym2.animate().translationY(keyboardSolvin_sym2.getHeight()).alpha(0.0f);
            keyboardSolvin_sym3.animate().translationY(keyboardSolvin_sym3.getHeight()).alpha(0.0f);
        }
    }

    public void adjustHiddenKeyboard() {
        menuParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        menuParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout_menu.setLayoutParams(menuParams);

        adjustView(height / 12);
        setKeyboardVisibility(false);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                keyboardSolvin_upp.setVisibility(View.INVISIBLE);
                keyboardSolvin_sym1.setVisibility(View.INVISIBLE);
                keyboardSolvin_sym2.setVisibility(View.INVISIBLE);
                keyboardSolvin_sym3.setVisibility(View.INVISIBLE);
            }
        }, 500);

        showSolvinKeyboard = false;
    }

    private void customizeKeyboardMenu(ImageButton[] menu) {
        for (int i = 0; i < menu.length; i++) {
            keyParams = menu[i].getLayoutParams();
            keyParams.width = width / 6;
            keyParams.height = height / 12;

            menu[i].setLayoutParams(keyParams);
            menu[i].setBackgroundResource(id.solvin.dev.R.drawable.custom_menu);
            menu[i].setOnClickListener(menuClickListener);
        }
        button_subscript.setEnabled(false);
        button_subscript.setAlpha(0.25f);

        button_superscript.setEnabled(false);
        button_superscript.setAlpha(0.25f);

        button_send.setEnabled(false);
        button_send.setAlpha(0.25f);
    }

    private void customizeKeyText(Button[] key, int mode) {
        if (mode == 0) {
            for (int i = 0; i < key.length; i++) {
                keyParams = key[i].getLayoutParams();
                keyParams.width = width / 10;
                keyParams.height = height / 14;

                key[i].setLayoutParams(keyParams);
                key[i].setBackgroundResource(id.solvin.dev.R.drawable.custom_key);
                key[i].setTypeface(customFont);
                key[i].setSoundEffectsEnabled(false);
                key[i].setOnClickListener(keyClickListener);
            }
        } else {
            for (int i = 0; i < key.length; i++) {
                keyParams = key[i].getLayoutParams();
                if (i < key.length - 2) {
                    keyParams.width = width / 10;
                } else if (i == key.length - 2) {
                    keyParams.width = (width / 20) * 3;
                } else {
                    keyParams.width = (width / 10) * 2;
                }
                keyParams.height = height / 14;

                key[i].setLayoutParams(keyParams);
                key[i].setBackgroundResource(id.solvin.dev.R.drawable.custom_key);
                key[i].setTypeface(customFont);
                key[i].setSoundEffectsEnabled(false);
                key[i].setOnClickListener(keyClickListener);
            }
        }
        key_symbol_sym1.getBackground().setColorFilter(Color.parseColor("#4DA8E7"), PorterDuff.Mode.MULTIPLY);
        key_symbol_sym2.getBackground().setColorFilter(Color.parseColor("#4DA8E7"), PorterDuff.Mode.MULTIPLY);
        key_symbol_sym3.getBackground().setColorFilter(Color.parseColor("#4DA8E7"), PorterDuff.Mode.MULTIPLY);

        key_variable_sym1.getBackground().setColorFilter(Color.parseColor("#4DA8E7"), PorterDuff.Mode.MULTIPLY);
        key_variable_sym2.getBackground().setColorFilter(Color.parseColor("#4DA8E7"), PorterDuff.Mode.MULTIPLY);
        key_variable_sym3.getBackground().setColorFilter(Color.parseColor("#4DA8E7"), PorterDuff.Mode.MULTIPLY);
    }

    private void customizeKeyImage(ImageButton[] specialKey, int mode) {
        if (mode == 0) {
            for (int i = 0; i < specialKey.length; i++) {
                keyParams = specialKey[i].getLayoutParams();
                if (i != 3) {
                    if (i == 0 || i == 1) {
                        keyParams.width = (width / 20) * 3;
                    } else {
                        keyParams.width = (width / 10) * 2;
                    }
                } else {
                    keyParams.width = (width / 10) * 6;
                }
                keyParams.height = height / 14;

                specialKey[i].setLayoutParams(keyParams);
                specialKey[i].setBackgroundResource(id.solvin.dev.R.drawable.custom_key);
                specialKey[i].setSoundEffectsEnabled(false);
                specialKey[i].setOnClickListener(keyClickListener);
            }
        } else {
            for (int i = 0; i < specialKey.length; i++) {
                keyParams = specialKey[i].getLayoutParams();
                if (i != 1) {
                    if (i == 0) {
                        keyParams.width = (width / 20) * 3;
                    } else {
                        keyParams.width = (width / 10) * 2;
                    }
                } else {
                    keyParams.width = (width / 10) * 6;
                }
                keyParams.height = height / 14;

                specialKey[i].setLayoutParams(keyParams);
                specialKey[i].setBackgroundResource(id.solvin.dev.R.drawable.custom_key);
                specialKey[i].setSoundEffectsEnabled(false);
                specialKey[i].setOnClickListener(keyClickListener);
            }
        }
        key_low.getBackground().setColorFilter(Color.parseColor("#4DA8E7"), PorterDuff.Mode.MULTIPLY);
        key_symbol_low.getBackground().setColorFilter(Color.parseColor("#4DA8E7"), PorterDuff.Mode.MULTIPLY);
        key_symbol_upp.getBackground().setColorFilter(Color.parseColor("#4DA8E7"), PorterDuff.Mode.MULTIPLY);
    }

    private View.OnClickListener menuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == id.solvin.dev.R.id.button_keyboard_mode) {
                button_keyboard_mode.setEnabled(false);
                setMode();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button_keyboard_mode.setEnabled(true);
                    }
                }, 250);

                button_subscript.setImageResource(id.solvin.dev.R.drawable.ic_subscript_dark);
                button_superscript.setImageResource(id.solvin.dev.R.drawable.ic_superscript_dark);

                if (!solvinKeyboardActived) {
                    solvinKeyboardActived = true;
                } else {
                    solvinKeyboardActived = false;
                }
                superScript_mode = false;
                subScript_mode = false;
            } else if (v.getId() == id.solvin.dev.R.id.button_subscript) {
                if (subScript_mode) {
                    button_subscript.setImageResource(id.solvin.dev.R.drawable.ic_subscript_dark);
                    subScript_mode = false;
                } else {
                    toast = Toast.makeText(getActivity(), "Aktivasi Penulisan Subscript", Toast.LENGTH_SHORT);
                    applicationTool.resizeToast(toast);
                    toast.show();

                    button_subscript.setImageResource(id.solvin.dev.R.drawable.ic_subscript_primary);
                    button_superscript.setImageResource(id.solvin.dev.R.drawable.ic_superscript_dark);
                    subScript_mode = true;
                }
                superScript_mode = false;
            } else if (v.getId() == id.solvin.dev.R.id.button_superscript) {
                if (superScript_mode) {
                    button_superscript.setImageResource(id.solvin.dev.R.drawable.ic_superscript_dark);
                    superScript_mode = false;
                } else {
                    toast = Toast.makeText(getActivity(), "Aktivasi Penulisan Superscript", Toast.LENGTH_SHORT);
                    applicationTool.resizeToast(toast);
                    toast.show();
                    button_subscript.setImageResource(id.solvin.dev.R.drawable.ic_subscript_dark);
                    button_superscript.setImageResource(id.solvin.dev.R.drawable.ic_superscript_primary);
                    superScript_mode = true;
                }
                subScript_mode = false;
            } else if (v.getId() == id.solvin.dev.R.id.button_camera) {
                startMediaActivity(CAPTURE_IMAGE);
            } else if (v.getId() == id.solvin.dev.R.id.button_upload) {
                startMediaActivity(PICK_IMAGE);
            } else if (v.getId() == id.solvin.dev.R.id.button_send) {
                backToActivity();
            }
        }
    };

    private View.OnClickListener keyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == id.solvin.dev.R.id.button_zero_low || v.getId() == id.solvin.dev.R.id.button_zero_upp ||
                    v.getId() == id.solvin.dev.R.id.button_zero_sym1 || v.getId() == id.solvin.dev.R.id.button_zero_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_zero_sym3) {
                isTextSelected();
                setCharacter("0");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_one_low || v.getId() == id.solvin.dev.R.id.button_one_upp ||
                    v.getId() == id.solvin.dev.R.id.button_one_sym1 || v.getId() == id.solvin.dev.R.id.button_one_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_one_sym3) {
                isTextSelected();
                setCharacter("1");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_two_low || v.getId() == id.solvin.dev.R.id.button_two_upp ||
                    v.getId() == id.solvin.dev.R.id.button_two_sym1 || v.getId() == id.solvin.dev.R.id.button_two_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_two_sym3) {
                isTextSelected();
                setCharacter("2");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_three_low || v.getId() == id.solvin.dev.R.id.button_three_upp ||
                    v.getId() == id.solvin.dev.R.id.button_three_sym1 || v.getId() == id.solvin.dev.R.id.button_three_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_three_sym3) {
                isTextSelected();
                setCharacter("3");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_four_low || v.getId() == id.solvin.dev.R.id.button_four_upp ||
                    v.getId() == id.solvin.dev.R.id.button_four_sym1 || v.getId() == id.solvin.dev.R.id.button_four_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_four_sym3) {
                isTextSelected();
                setCharacter("4");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_five_low || v.getId() == id.solvin.dev.R.id.button_five_upp ||
                    v.getId() == id.solvin.dev.R.id.button_five_sym1 || v.getId() == id.solvin.dev.R.id.button_five_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_five_sym3) {
                isTextSelected();
                setCharacter("5");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_six_low || v.getId() == id.solvin.dev.R.id.button_six_upp ||
                    v.getId() == id.solvin.dev.R.id.button_six_sym1 || v.getId() == id.solvin.dev.R.id.button_six_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_six_sym3) {
                isTextSelected();
                setCharacter("6");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_seven_low || v.getId() == id.solvin.dev.R.id.button_seven_upp ||
                    v.getId() == id.solvin.dev.R.id.button_seven_sym1 || v.getId() == id.solvin.dev.R.id.button_seven_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_seven_sym3) {
                isTextSelected();
                setCharacter("7");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_eight_low || v.getId() == id.solvin.dev.R.id.button_eight_upp ||
                    v.getId() == id.solvin.dev.R.id.button_eight_sym1 || v.getId() == id.solvin.dev.R.id.button_eight_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_eight_sym3) {
                isTextSelected();
                setCharacter("8");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_nine_low || v.getId() == id.solvin.dev.R.id.button_nine_upp ||
                    v.getId() == id.solvin.dev.R.id.button_nine_sym1 || v.getId() == id.solvin.dev.R.id.button_nine_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_nine_sym3) {
                isTextSelected();
                setCharacter("9");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            }
//
            else if (v.getId() == id.solvin.dev.R.id.button_a) {
                isTextSelected();
                setCharacter("a");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_b) {
                isTextSelected();
                setCharacter("b");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_c) {
                isTextSelected();
                setCharacter("c");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_d) {
                isTextSelected();
                setCharacter("d");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_e) {
                isTextSelected();
                setCharacter("e");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_f) {
                isTextSelected();
                setCharacter("f");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_g) {
                isTextSelected();
                setCharacter("g");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_h) {
                isTextSelected();
                setCharacter("h");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_i) {
                isTextSelected();
                setCharacter("i");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_j) {
                isTextSelected();
                setCharacter("j");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_k) {
                isTextSelected();
                setCharacter("k");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_l) {
                isTextSelected();
                setCharacter("l");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_m) {
                isTextSelected();
                setCharacter("m");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_n) {
                isTextSelected();
                setCharacter("n");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_o) {
                isTextSelected();
                setCharacter("o");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_p) {
                isTextSelected();
                setCharacter("p");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_q) {
                isTextSelected();
                setCharacter("q");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_r) {
                isTextSelected();
                setCharacter("r");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_s) {
                isTextSelected();
                setCharacter("s");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_t) {
                isTextSelected();
                setCharacter("t");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_u) {
                isTextSelected();
                setCharacter("u");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_v) {
                isTextSelected();
                setCharacter("v");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_w) {
                isTextSelected();
                setCharacter("w");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_x) {
                isTextSelected();
                setCharacter("x");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_y) {
                isTextSelected();
                setCharacter("y");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_z) {
                isTextSelected();
                setCharacter("z");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            }
//
            else if (v.getId() == id.solvin.dev.R.id.button_A) {
                isTextSelected();
                setCharacter("A");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_B) {
                isTextSelected();
                setCharacter("B");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_C) {
                isTextSelected();
                setCharacter("C");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_D) {
                isTextSelected();
                setCharacter("D");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_E) {
                isTextSelected();
                setCharacter("E");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_F) {
                isTextSelected();
                setCharacter("F");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_G) {
                isTextSelected();
                setCharacter("G");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_H) {
                isTextSelected();
                setCharacter("H");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_I) {
                isTextSelected();
                setCharacter("I");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_J) {
                isTextSelected();
                setCharacter("J");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_K) {
                isTextSelected();
                setCharacter("K");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_L) {
                isTextSelected();
                setCharacter("L");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_M) {
                isTextSelected();
                setCharacter("M");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_N) {
                isTextSelected();
                setCharacter("N");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_O) {
                isTextSelected();
                setCharacter("O");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_P) {
                isTextSelected();
                setCharacter("P");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_Q) {
                isTextSelected();
                setCharacter("Q");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_R) {
                isTextSelected();
                setCharacter("R");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_S) {
                isTextSelected();
                setCharacter("S");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_T) {
                isTextSelected();
                setCharacter("T");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_U) {
                isTextSelected();
                setCharacter("U");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_V) {
                isTextSelected();
                setCharacter("V");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_W) {
                isTextSelected();
                setCharacter("W");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_X) {
                isTextSelected();
                setCharacter("X");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_Y) {
                isTextSelected();
                setCharacter("Y");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_Z) {
                isTextSelected();
                setCharacter("Z");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            }
//
            else if (v.getId() == id.solvin.dev.R.id.button_plus) {
                isTextSelected();
                setCharacter("\u002B");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_subtract) {
                isTextSelected();
                setCharacter("\u2212");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_multiply1) {
                isTextSelected();
                setCharacter("\u00D7");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_division) {
                isTextSelected();
                setCharacter("\u00F7");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_equality) {
                isTextSelected();
                setCharacter("\u003D");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_inequality) {
                isTextSelected();
                setCharacter("\u2260");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_sqrt) {
                isTextSelected();
                setCharacter("\u221A" + "\u0028" + "\u0029");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_percentage) {
                isTextSelected();
                setCharacter("\u0025");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_permille) {
                isTextSelected();
                setCharacter("\u2030");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_plus_minus) {
                isTextSelected();
                setCharacter("\u00B1");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_multiply2) {
                isTextSelected();
                setCharacter("\u002A");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_fraction) {
                isTextSelected();
                setCharacter("/");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_approximation) {
                isTextSelected();
                setCharacter("\u2248");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_identical) {
                isTextSelected();
                setCharacter("\u2261");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_less) {
                isTextSelected();
                setCharacter("\u003C");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_greater) {
                isTextSelected();
                setCharacter("\u003E");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_less_equality) {
                isTextSelected();
                setCharacter("\u2264");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_greater_equality) {
                isTextSelected();
                setCharacter("\u2265");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_period) {
                isTextSelected();
                setCharacter("\u002E");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_comma) {
                isTextSelected();
                setCharacter("\u002C");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_colon) {
                isTextSelected();
                setCharacter(":");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_left_bracket) {
                isTextSelected();
                setCharacter("\u0028");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_right_bracket) {
                isTextSelected();
                setCharacter("\u0029");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_left_curly_bracket) {
                isTextSelected();
                setCharacter("\u007B");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_right_curly_bracket) {
                isTextSelected();
                setCharacter("\u007D");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_left_square_bracket) {
                isTextSelected();
                setCharacter("\u005B");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_right_square_bracket) {
                isTextSelected();
                setCharacter("\u005D");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            }
//
            else if (v.getId() == id.solvin.dev.R.id.button_sum) {
                isTextSelected();
                setCharacter("\u03A3");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_product) {
                isTextSelected();
                setCharacter("\u220F");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_left_floor) {
                isTextSelected();
                setCharacter("\u230A");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_right_floor) {
                isTextSelected();
                setCharacter("\u230B");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_left_ceiling) {
                isTextSelected();
                setCharacter("\u2308");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_right_ceiling) {
                isTextSelected();
                setCharacter("\u2309");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_absolute) {
                isTextSelected();
                setCharacter("\u007C");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_intersection) {
                isTextSelected();
                setCharacter("\u2229");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_union) {
                isTextSelected();
                setCharacter("\u222A");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_empty_set) {
                isTextSelected();
                setCharacter("\u2205");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_element_of) {
                isTextSelected();
                setCharacter("\u2208");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_not_element_of) {
                isTextSelected();
                setCharacter("\u2209");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_subset_of) {
                isTextSelected();
                setCharacter("\u2282");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_not_subset_of) {
                isTextSelected();
                setCharacter("\u2284");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_triangle) {
                isTextSelected();
                setCharacter("\u0394");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_nabla) {
                isTextSelected();
                setCharacter("\u2207");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_congruent) {
                isTextSelected();
                setCharacter("\u2245");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_similarity) {
                isTextSelected();
                setCharacter("\u007E");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_angle) {
                isTextSelected();
                setCharacter("\u2220");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_degree) {
                isTextSelected();
                setCharacter("\u00B0");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_universal) {
                isTextSelected();
                setCharacter("\u2200");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_existential) {
                isTextSelected();
                setCharacter("\u2203");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_negation) {
                isTextSelected();
                setCharacter("\u00AC");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_conjunction) {
                isTextSelected();
                setCharacter("\u2227");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_disjunction) {
                isTextSelected();
                setCharacter("\u2228");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_implication) {
                isTextSelected();
                setCharacter("\u2192");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_biimplication) {
                isTextSelected();
                setCharacter("\u2194");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            }
//
            else if (v.getId() == id.solvin.dev.R.id.button_xor) {
                isTextSelected();
                setCharacter("\u2295");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_therefore) {
                isTextSelected();
                setCharacter("\u2234");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_perpendicular) {
                isTextSelected();
                setCharacter("⊥");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_parallel) {
                isTextSelected();
                setCharacter("\u2016");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_factorial) {
                isTextSelected();
                setCharacter("\u0021");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_dot) {
                isTextSelected();
                setCharacter("\u25E6");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_integral) {
                isTextSelected();
                setCharacter("\u222B");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_derivative) {
                isTextSelected();
                setCharacter("\u2202");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_alpha) {
                isTextSelected();
                setCharacter("\u03B1");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_beta) {
                isTextSelected();
                setCharacter("\u03B2");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_gamma) {
                isTextSelected();
                setCharacter("\u03B3");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_delta) {
                isTextSelected();
                setCharacter("\u03B4");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_epsilon) {
                isTextSelected();
                setCharacter("\u03B5");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_theta) {
                isTextSelected();
                setCharacter("\u03B8");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_mu) {
                isTextSelected();
                setCharacter("\u03BC");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_pi) {
                isTextSelected();
                setCharacter("\u03C0");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_rho) {
                isTextSelected();
                setCharacter("\u03C1");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_sigma) {
                isTextSelected();
                setCharacter("\u03C3");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_tau) {
                isTextSelected();
                setCharacter("\u03C4");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_phi1) {
                isTextSelected();
                setCharacter("\u03C6");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_phi2) {
                isTextSelected();
                setCharacter("Φ");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_omega) {
                isTextSelected();
                setCharacter("\u03C9");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_eta) {
                isTextSelected();
                setCharacter("\u03B7");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_lamda) {
                isTextSelected();
                setCharacter("\u03BB");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_ohm) {
                isTextSelected();
                setCharacter("\u03A9");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_armstrong) {
                isTextSelected();
                setCharacter("\u212B");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_infinity) {
                isTextSelected();
                setCharacter("\u221E");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            }
//
            else if (v.getId() == id.solvin.dev.R.id.button_low) {
                keyboardSolvin_low.setVisibility(View.VISIBLE);
                keyboardSolvin_upp.setVisibility(View.INVISIBLE);
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_upp) {
                keyboardSolvin_low.setVisibility(View.INVISIBLE);
                keyboardSolvin_upp.setVisibility(View.VISIBLE);
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_symbol_sym1) {
                keyboardSolvin_sym1.setVisibility(View.INVISIBLE);
                keyboardSolvin_sym2.setVisibility(View.VISIBLE);
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_symbol_sym2) {
                keyboardSolvin_sym2.setVisibility(View.INVISIBLE);
                keyboardSolvin_sym3.setVisibility(View.VISIBLE);
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_symbol_sym3) {
                keyboardSolvin_sym1.setVisibility(View.VISIBLE);
                keyboardSolvin_sym3.setVisibility(View.INVISIBLE);
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_symbol_low || v.getId() == id.solvin.dev.R.id.button_symbol_upp) {
                if (keyboardSolvin_low.isShown()) {
                    keyboardSolvin_low.setVisibility(View.INVISIBLE);
                } else if (keyboardSolvin_upp.isShown()) {
                    keyboardSolvin_upp.setVisibility(View.INVISIBLE);
                }
                keyboardSolvin_sym1.setVisibility(View.VISIBLE);
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_variable_sym1) {
                keyboardSolvin_low.setVisibility(View.VISIBLE);
                keyboardSolvin_sym1.setVisibility(View.INVISIBLE);
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_variable_sym2) {
                keyboardSolvin_low.setVisibility(View.VISIBLE);
                keyboardSolvin_sym2.setVisibility(View.INVISIBLE);
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_variable_sym3) {
                keyboardSolvin_low.setVisibility(View.VISIBLE);
                keyboardSolvin_sym3.setVisibility(View.INVISIBLE);
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
            }
//
            else if (v.getId() == id.solvin.dev.R.id.button_space_low || v.getId() == id.solvin.dev.R.id.button_space_upp ||
                    v.getId() == id.solvin.dev.R.id.button_space_sym1 || v.getId() == id.solvin.dev.R.id.button_space_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_space_sym3) {
                isTextSelected();
                setCharacter(" ");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR, 1.0f);
            } else if (v.getId() == id.solvin.dev.R.id.button_enter_low || v.getId() == id.solvin.dev.R.id.button_enter_upp ||
                    v.getId() == id.solvin.dev.R.id.button_enter_sym1 || v.getId() == id.solvin.dev.R.id.button_enter_sym2 ||
                    v.getId() == id.solvin.dev.R.id.button_enter_sym3) {
                isTextSelected();
                setCharacter("\n");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN, 1.0f);
            }
        }
    };

    private View.OnTouchListener deleteTouchListener = new View.OnTouchListener() {
        private static final int MIN_CLICK_DURATION = 250;
        private long startClickTime;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.onTouchEvent(event);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!longClickActive) {
                        if (!isTextSelected()) {
                            characterDeletion(0, 1);
                            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE, 1.0f);
                        }
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        longClickActive = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (longClickActive) {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration >= MIN_CLICK_DURATION) {
                            fastDeleting();

                            longPressActive = true;
                            longClickActive = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    range = 1;
                    if (longPressActive) {
                        timer.cancel();
                    }
                    longPressActive = false;
                    longClickActive = false;
                    break;
            }
            return true;
        }
    };

    private void fastDeleting() {
        timer = new CountDownTimer(999999999, 250) {
            @Override
            public void onTick(long millisUntilFinished) {
                characterDeletion(range, range);
                if (range < 50) {
                    range += range;
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void setCharacter(String character) {
        spannableCharacter = new SpannableStringBuilder(character);
        if (character.equals("→") || character.equals("↔")) {
            spannableCharacter.setSpan(new CustomTypefaceSpan("", specialFont), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (superScript_mode) {
            setSuperScript(spannableCharacter);
        } else if (subScript_mode) {
            setSubScript(spannableCharacter);
        }
        typeCharacter(spannableCharacter);
    }

    private void setSuperScript(SpannableStringBuilder character) {
        if (character.charAt(0) == '\u221A') {
            character.setSpan(new SuperscriptSpan(), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            character.setSpan(new RelativeSizeSpan(0.75f), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            character.setSpan(new SuperscriptSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            character.setSpan(new RelativeSizeSpan(0.75f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void setSubScript(SpannableStringBuilder character) {
        if (character.charAt(0) == '\u221A') {
            character.setSpan(new SubscriptSpan(), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            character.setSpan(new RelativeSizeSpan(0.75f), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            character.setSpan(new SubscriptSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            character.setSpan(new RelativeSizeSpan(0.75f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    //    INTERFACE
    private FragmentSolvinKeyboard.OnKeyboardTask mKeyboardTask;

    public interface OnKeyboardTask {
        void ITypeCharacter(SpannableStringBuilder character);

        void IAdjustView(int newHeight);

        void ICharacterDeletion(int limit, int n);

        boolean IIsTextSelected();

        void IStartMediaActivity(int ACTIVITY_CODE);

        void IBackToActivity();
    }

    public void setKeyboardTask(FragmentSolvinKeyboard.OnKeyboardTask listener) {
        mKeyboardTask = listener;
    }

    private void typeCharacter(SpannableStringBuilder character) {
        if (mKeyboardTask != null) {
            mKeyboardTask.ITypeCharacter(character);
        }
    }

    private void adjustView(int newHeight) {
        if (mKeyboardTask != null) {
            mKeyboardTask.IAdjustView(newHeight);
        }
    }

    private void characterDeletion(int limit, int n) {
        if (mKeyboardTask != null) {
            mKeyboardTask.ICharacterDeletion(limit, n);
        }
    }

    private boolean isTextSelected() {
        if (mKeyboardTask != null) {
            return mKeyboardTask.IIsTextSelected();
        } else {
            return false;
        }
    }

    private void startMediaActivity(int ACTIVITY_CODE) {
        if (mKeyboardTask != null) {
            mKeyboardTask.IStartMediaActivity(ACTIVITY_CODE);
        }
    }

    private void backToActivity() {
        if (mKeyboardTask != null) {
            mKeyboardTask.IBackToActivity();
        }
    }
}