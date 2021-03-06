/*
 * Copyright (C) 2017 Vincent Breitmoser <v.breitmoser@mugenguild.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sufficientlysecure.keychain.ui.keyview.view;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.sufficientlysecure.keychain.R;
import org.sufficientlysecure.keychain.ui.adapter.LinkedIdsAdapter;
import org.sufficientlysecure.keychain.ui.keyview.presenter.LinkedIdentitiesPresenter.LinkedIdsClickListener;
import org.sufficientlysecure.keychain.ui.keyview.presenter.LinkedIdentitiesPresenter.LinkedIdsMvpView;


public class LinkedIdentitiesCardView extends CardView implements LinkedIdsMvpView {
    private ListView vLinkedIds;
    private TextView vLinkedIdsEmpty;

    private LinkedIdsClickListener linkedIdsClickListener;

    public LinkedIdentitiesCardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.linked_identities_card, this, true);

        vLinkedIds = (ListView) view.findViewById(R.id.view_key_linked_ids);
        vLinkedIdsEmpty = (TextView)  view.findViewById(R.id.view_key_linked_ids_empty);
        Button linkedIdsAddButton = (Button) view.findViewById(R.id.view_key_card_linked_ids_add);

        linkedIdsAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linkedIdsClickListener != null) {
                    linkedIdsClickListener.onClickAddIdentity();
                }
            }
        });

        vLinkedIds.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (linkedIdsClickListener != null) {
                    linkedIdsClickListener.onLinkedIdItemClick(position);
                }
            }
        });
    }

    @Override
    public void setSystemContactClickListener(LinkedIdsClickListener linkedIdsClickListener) {
        this.linkedIdsClickListener = linkedIdsClickListener;
    }

    @Override
    public void setLinkedIdsAdapter(LinkedIdsAdapter linkedIdsAdapter) {
        vLinkedIds.setAdapter(linkedIdsAdapter);
    }

    @Override
    public void showCard(boolean visible) {
        setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showEmptyView(boolean visible) {
        vLinkedIdsEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
