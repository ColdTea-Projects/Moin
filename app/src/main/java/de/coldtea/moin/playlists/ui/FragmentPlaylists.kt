package de.coldtea.moin.playlists.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.coldtea.moin.R

class FragmentPlaylists : Fragment() {

    private lateinit var viewModelPlaylists: ViewModelPlaylists

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelPlaylists =
            ViewModelProviders.of(this).get(ViewModelPlaylists::class.java)
        val root = inflater.inflate(R.layout.moin_fragment_playlists, container, false)
        val textView: TextView = root.findViewById(R.id.tv_playlisys)
        viewModelPlaylists.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}