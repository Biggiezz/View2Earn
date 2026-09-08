package com.vuhongcat.view2earn.app.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.vuhongcat.view2earn.app.R;
import com.vuhongcat.view2earn.app.models.Bank;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class BankDropdownAdapter extends ArrayAdapter<Bank> implements Filterable {

    private final Context context;
    private final List<Bank> originalList;
    private final List<Bank> filteredList;
    private final BankFilter bankFilter = new BankFilter();

    public BankDropdownAdapter(@NonNull Context context, @NonNull List<Bank> banks) {
        super(context, R.layout.item_bank_dropdown, new ArrayList<>(banks));
        this.context = context;
        this.originalList = new ArrayList<>(banks);
        this.filteredList = new ArrayList<>(banks);
    }

    public void updateBanks(List<Bank> newBanks) {
        originalList.clear();
        if (newBanks != null) {
            originalList.addAll(newBanks);
        }
        filteredList.clear();
        filteredList.addAll(originalList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Nullable
    @Override
    public Bank getItem(int position) {
        if (position >= 0 && position < filteredList.size()) {
            return filteredList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bank_dropdown, parent, false);
            holder = new ViewHolder();
            holder.ivLogo = convertView.findViewById(R.id.ivBankLogo);
            holder.tvShortName = convertView.findViewById(R.id.tvBankShortName);
            holder.tvFullName = convertView.findViewById(R.id.tvBankFullName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bank bank = getItem(position);
        if (bank != null) {
            holder.tvShortName.setText(bank.getShortName());
            holder.tvFullName.setText(bank.getName());

            if (!TextUtils.isEmpty(bank.getLogo())) {
                Glide.with(context)
                        .load(bank.getLogo())
                        .fitCenter()
                        .into(holder.ivLogo);
            } else {
                holder.ivLogo.setImageDrawable(null);
            }
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return bankFilter;
    }

    private static class ViewHolder {
        ImageView ivLogo;
        TextView tvShortName;
        TextView tvFullName;
    }

    private class BankFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                results.values = new ArrayList<>(originalList);
                results.count = originalList.size();
                return results;
            }

            String query = removeAccents(constraint.toString().trim().toLowerCase(Locale.ROOT));
            List<Bank> matched = new ArrayList<>();

            for (Bank b : originalList) {
                String shortN = removeAccents(b.getShortName() != null ? b.getShortName().toLowerCase(Locale.ROOT) : "");
                String code = removeAccents(b.getCode() != null ? b.getCode().toLowerCase(Locale.ROOT) : "");
                String fullN = removeAccents(b.getName() != null ? b.getName().toLowerCase(Locale.ROOT) : "");

                if (shortN.contains(query) || code.contains(query) || fullN.contains(query)) {
                    matched.add(b);
                }
            }

            results.values = matched;
            results.count = matched.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList.clear();
            if (results != null && results.values != null) {
                filteredList.addAll((List<Bank>) results.values);
            }
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            if (resultValue instanceof Bank) {
                return ((Bank) resultValue).getDisplayName();
            }
            return super.convertResultToString(resultValue);
        }
    }

    private static String removeAccents(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replace("Đ", "D").replace("đ", "d");
    }
}
