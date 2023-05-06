package com.tai.api_chatgpt.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tai.api_chatgpt.R;
import com.tai.api_chatgpt.adapter.MessageAdapter;
import com.tai.api_chatgpt.databinding.ActivityMainBinding;
import com.tai.api_chatgpt.model.Message;

import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private SharedPreferences sharedPreferences;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }

    private void initViews() {
        showDiaNoInternet();
        initAdapter();
        clickSend();
        clickTvEx();
        clickVoice();
        loadData();
        search();
    }

    private void showDiaNoInternet() {
        NoInternetDialogSignal.Builder builder = new NoInternetDialogSignal.Builder(MainActivity.this, getLifecycle());
        DialogPropertiesSignal properties = builder.getDialogProperties();
        //Nhận thông tin về trạng thái kết nối Internet hiện tại (có hoặc không).
        properties.setConnectionCallback(new ConnectionCallback() { // Optional dialog
            @Override
            public void hasActiveConnection(boolean hasActiveConnection) {
                //Chỉ ra trạng thái kết nối Internet hiện tại.
            }
        });

        properties.setCancelable(false);
        properties.setNoInternetConnectionTitle("No Internet");
        properties.setNoInternetConnectionMessage("Please check your Internet connection and try again!");
        properties.setShowInternetOnButtons(true);
        properties.setPleaseTurnOnText("Please turn on");
        properties.setWifiOnButtonText("Wifi");
        properties.setMobileDataOnButtonText("Mobile data");

        properties.setOnAirplaneModeTitle("No Internet");
        properties.setOnAirplaneModeMessage("You have turned on the airplane mode!");
        properties.setPleaseTurnOffText("Please turn off");
        properties.setAirplaneModeOffButtonText("Airplane mode");
        properties.setShowAirplaneModeOffButtons(true);
        builder.build();
    }

    private void search() {
        binding.svMessage.clearFocus();
        binding.svMessage.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });
    }

    private void filterList(String newText) {
        List<Message> filteredList = new ArrayList<>();
        for (Message message : messageList) {
            if (message.getMessage().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(message);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Data Not Found", Toast.LENGTH_SHORT).show();
        } else {
            messageAdapter.setFilteredList(filteredList);
        }
    }

    private void clickTvEx() {
        binding.btEx1.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(MainActivity.this, androidx.appcompat.R.anim.abc_fade_in);
            binding.btEx1.startAnimation(animation);
            String question = getResources().getString(R.string.txt_ex1);
            addToChat(question, Message.SENT_BY_HUMAN);
            callAPI(question);

            binding.svMessage.setVisibility(View.VISIBLE);
            binding.ivSend.setVisibility(View.GONE);
            binding.lnEx.setVisibility(View.GONE);
        });

        binding.btEx2.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(MainActivity.this, androidx.appcompat.R.anim.abc_fade_in);
            binding.btEx2.startAnimation(animation);
            String question = getResources().getString(R.string.txt_ex2);
            addToChat(question, Message.SENT_BY_HUMAN);
            callAPI(question);

            binding.svMessage.setVisibility(View.VISIBLE);
            binding.ivSend.setVisibility(View.GONE);
            binding.lnEx.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Lưu danh sách tin nhắn vào SharedPreferences
        sharedPreferences = getSharedPreferences("chatbot_openai", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("message_list", messageAdapter.getData());
        editor.apply();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadData() {

        // Load list message from SharedPreferences
        sharedPreferences = getSharedPreferences("chatbot_openai", MODE_PRIVATE);
        String messageListJson = sharedPreferences.getString("message_list", "");

        if (!messageListJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Message>>() {
            }.getType();
            List<Message> savedMessages = gson.fromJson(messageListJson, type);
            messageList.addAll(savedMessages);
            messageAdapter.notifyDataSetChanged();

            binding.svMessage.setVisibility(View.VISIBLE);
            binding.lnEx.setVisibility(View.GONE);
        } else {
            binding.svMessage.setVisibility(View.GONE);
            binding.lnEx.setVisibility(View.VISIBLE);
        }
    }

    private void initAdapter() {
        messageList = new ArrayList<>();

        messageAdapter = new MessageAdapter(messageList);
        binding.rvOutput.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.rvOutput.setLayoutManager(linearLayoutManager);
    }

    private void clickSend() {
        binding.ivSend.setOnClickListener((v) -> {
            Animation animation = AnimationUtils.loadAnimation(MainActivity.this, androidx.appcompat.R.anim.abc_fade_in);
            binding.ivSend.startAnimation(animation);
            String question = binding.edtMessage.getText().toString().trim();
            if (!question.isEmpty()) {
                addToChat(question, Message.SENT_BY_HUMAN);
                binding.edtMessage.setText("");
                callAPI(question);
                binding.lnEx.setVisibility(View.GONE);
                binding.ivSend.setVisibility(View.GONE);
                binding.svMessage.setVisibility(View.VISIBLE);
                hideSoftKeyboard();
            } else {
                Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            binding.rvOutput.smoothScrollToPosition(messageAdapter.getItemCount());
        });
    }

    void callAPI(String question) {
        messageList.add(new Message("Please waiting for answer....", Message.SENT_BY_AI));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "text-davinci-003");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens", 2000);
            jsonBody.put("temperature", 0.9);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Tạo một request gửi đến server
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        // Gửi Request và xử lý response từ server
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Error due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String responseBody = response.body().string();
                    addResponse("Failed response due to " + responseBody);
                }
            }
        });
    }

    void addResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addToChat(response, Message.SENT_BY_AI);
        runOnUiThread(() -> binding.ivSend.setVisibility(View.VISIBLE));

    }

    private void clickVoice() {
        binding.ivVoice.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(MainActivity.this, androidx.appcompat.R.anim.abc_fade_in);
            binding.ivVoice.startAnimation(animation);
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e) {
                clickSend();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                binding.edtMessage.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }

    //ẩn key board
    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
