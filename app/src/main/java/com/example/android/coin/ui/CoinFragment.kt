package com.example.android.coin.ui

/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */



import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.coin.R
import com.example.android.coin.databinding.CoinItemBinding
import com.example.android.coin.databinding.FragmentCoinBinding
import com.example.android.coin.domain.Coin
import com.example.android.coin.viewmodels.CoinViewModel

/**
 * Show a list of DevBytes on screen.
 */
class CoinFragment : Fragment() {

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onViewCreated(), which we
     * do in this Fragment.
     */
    private val viewModel: CoinViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        //The ViewModelProviders (plural) is deprecated.
        //ViewModelProviders.of(this, DevByteViewModel.Factory(activity.application)).get(DevByteViewModel::class.java)
        ViewModelProvider(this, CoinViewModel.Factory(activity.application)).get(CoinViewModel::class.java)

    }

    /**
     * RecyclerView Adapter for converting a list of Video to cards.
     */
    private var viewModelAdapter: CoinAdapter? = null

    /**
     * Called immediately after onCreateView() has returned, and fragment's
     * view hierarchy has been created.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.playlist.observe(viewLifecycleOwner, Observer<List<Coin>> { coins ->
            coins?.apply {
                viewModelAdapter?.coins = coins
            }
        })
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentCoinBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_coin,
            container,
            false)
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        viewModelAdapter = CoinAdapter(CoinClick {
            // When a video is clicked this block or lambda will be called by DevByteAdapter

            // context is not around, we can safely discard this click since the Fragment is no
            // longer on the screen
            val packageManager = context?.packageManager ?: return@CoinClick

            // Try to generate a direct intent to the YouTube app
            var intent = Intent(Intent.ACTION_VIEW, it.launchUri)
            if(intent.resolveActivity(packageManager) == null) {
                // YouTube app isn't found, use the web url
                intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
            }

            startActivity(intent)
        })

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        return binding.root
    }

    /**
     * Helper method to generate YouTube app links
     */
    private val Coin.launchUri: Uri
        get() {
            val httpUri = Uri.parse(url)
            return Uri.parse("vnd.youtube:" + httpUri.getQueryParameter("v"))
        }
}

/**
 * Click listener for Videos. By giving the block a name it helps a reader understand what it does.
 *
 */
class CoinClick(val block: (Coin) -> Unit) {
    /**
     * Called when a video is clicked
     *
     * @param coin the video that was clicked
     */
    fun onClick(coin: Coin) = block(coin)
}

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class CoinAdapter(val callback: CoinClick) : RecyclerView.Adapter<CoinViewHolder>() {

    /**
     * The videos that our Adapter will show
     */
    var coins: List<Coin> = emptyList()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val withDataBinding: CoinItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            CoinViewHolder.LAYOUT,
            parent,
            false)
        return CoinViewHolder(withDataBinding)
    }

    override fun getItemCount() = coins.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.coin = coins[position]
            it.coinCallback = callback
        }
    }

}

/**
 * ViewHolder for DevByte items. All work is done by data binding.
 */
class CoinViewHolder(val viewDataBinding: CoinItemBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.coin_item
    }
}
