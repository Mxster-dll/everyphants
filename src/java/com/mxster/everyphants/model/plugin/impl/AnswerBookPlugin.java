package com.mxster.everyphants.model.plugin.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class AnswerBookPlugin extends ReactivePlugin<String> {
    public AnswerBookPlugin() {
        super("答案之书", null);

        parsers.add(this::parseQuestion);
        formatters.add(this::getAnswer);
    }

    public String parseQuestion(String input) {
        boolean isQuestion = input.endsWith("?") || input.endsWith("？");

        return isQuestion ? input : null;
    }

    public Result getAnswer(String query) {
        final Random random = new Random();
        File file = new File("src/resources/答案之书.txt");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String answer = "在那之前，要多想";

            String line = reader.readLine();
            for (int cnt = 1; line != null; cnt++) {
                // 第 i 行以 1/i 的概率替换当前的选中，以此保证等概率抽取
                if (random.nextInt(cnt) == 0) {
                    answer = line;
                }

                line = reader.readLine();
            }

            return new Result(answer, "答案之书回应了你...", 1, null);
        } catch (IOException e) {
            String[] messages = {
                    "书页微微震颤，却未向此刻的风敞开……或许它还在倾听更迫切的问题。",
                    "一枚看不见的星尘落在封面上。待它消散时，书会再次应答你。",
                    "风正好翻过了几页空白——稍等片刻，答案正在穿过字里行间。",
                    "这一章被时光的丝线缝合了。不如换个问题，书会为你另启一页。",
                    "书页微光一闪，随即归于沉寂。"
            };

            int index = random.nextInt(messages.length);
            return new Result(messages[index], "无法打开答案之书.txt", 1, null);
        }

    }
}
