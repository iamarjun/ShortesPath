package com.example.shortespath

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_logs.view.*

class LogsAdapter(private val logs: ArrayList<Logs>): RecyclerView.Adapter<LogsAdapter.LogsViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LogsViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.layout_logs, p0, false)

        return LogsViewHolder(view)
    }

    override fun getItemCount(): Int = logs.size

    override fun onBindViewHolder(p0: LogsViewHolder, p1: Int) {

        p0.mOrigin.text = logs[p1].origin.toString()
        p0.mDestination.text = logs[p1].destination.toString()
        p0.mWayPoints.text = logs[p1].waypoints.toString()
        p0.mDistance.text = logs[p1].distance.toString()
        p0.mDuration.text = logs[p1].duration.toString()
    }

    inner class LogsViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        val mOrigin = view.origin
        val mDestination = view.destination
        val mWayPoints = view.waypoints
        val mDistance = view.distance
        val mDuration = view.duration

    }
}