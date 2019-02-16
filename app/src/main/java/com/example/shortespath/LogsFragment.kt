package com.example.shortespath

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_logs.view.*

class LogsFragment : Fragment(), MapsFragment.LogListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mView: View
    private lateinit var mLogs: ArrayList<Logs>
    private lateinit var adapter: LogsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_logs, container, false)

        mView.logs.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)

        mLogs = ArrayList()

        mView.swipe_refresh.setOnRefreshListener(this)

        MapsFragment.setListener(this)

        return mView
    }

    override fun onRefresh() {
        if (::adapter.isInitialized)
            adapter.notifyDataSetChanged()

        mView.swipe_refresh.isRefreshing = false
    }

    override fun onGettingLog(log: Logs) {

        mLogs.add(log)
        adapter = LogsAdapter(mLogs)
        mView.logs.adapter = adapter
    }

}
