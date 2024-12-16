package com.example.finapp.ui.tips_screens.adapter_tips;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finapp.databinding.TipItemBinding;
import com.example.finapp.model.Tip;


public class TipViewHolder extends RecyclerView.ViewHolder {
    private final TipItemBinding binding;
    private Tip tip;

    public TipViewHolder(TipItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void onBind(Tip data) {
        this.tip = data;

        // Используем привязку для установки значений
        binding.tvTitle.setText(tip.getTitle());
        binding.tvAdvice.setText(tip.getText());

    }

    public String parse(String input) {
        StringBuilder genresList = new StringBuilder();
        String regex = "genre=([a-zA-Zа-яА-Я]+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            genresList.append(matcher.group(1)).append(", ");
        }

        // Убираем последнюю запятую и пробел
        if (genresList.length() > 0) {
            genresList.setLength(genresList.length() - 2);
        }

        // Возвращаем первый жанр или пустую строку, если нет совпадений
        return genresList.length() > 0 ? genresList.toString().split(", ")[0] : "";
    }
}