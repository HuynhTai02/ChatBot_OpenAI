package com.tai.api_chatgpt.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tai.api_chatgpt.adapter.MessageAdapter;
import com.tai.api_chatgpt.databinding.ActivityMainBinding;
import com.tai.api_chatgpt.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    List<Message> messageList;
    MessageAdapter messageAdapter;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initAdapter();

        clickVoice();
        clickSend();
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
        binding.btSend.setOnClickListener((v) -> {
            String question = binding.edtMessgae.getText().toString().trim();
            addToChat(question, Message.SENT_BY_HUMAN);
            binding.edtMessgae.setText("");
            callAPI(question);
            binding.tvWelcome.setVisibility(View.GONE);
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
                .header("Authorization", "Bearer sk-xSM0ovvrN5Phwo3OAVnrT3BlbkFJNQ6wazdZSnYo0wp7")
                .post(body)
                .build();

        // Gửi Request và xử lý response từ server
        // một cách không đồng bộ với một hàm callback khi có một phản hồi.
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
                    addResponse("Failed response due to " + response.body().toString());
                }
            }
        });
    }

    void addResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addToChat(response, Message.SENT_BY_AI);
    }

    private void clickVoice() {
        binding.btVoice.setOnClickListener(v -> {
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
                binding.edtMessgae.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }
}