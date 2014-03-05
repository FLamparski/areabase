package lamparski.areabase.cards;

import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

import lamparski.areabase.R;

/**
 * Created by Minkovsky on 05/03/14.
 */
public class AreaRankCard extends RecyclableCard {
    @Override
    protected void applyTo(View convertView) {
        ((TextView) convertView.findViewById(R.id.card_arearank_text)).setText(titlePlay);
        ((TextView) convertView.findViewById(R.id.card_arearank_score)).setText(description);
    }

    @Override
    protected int getCardLayoutId() {
        return R.layout.card_arearank;
    }
}
