package com.hcdisat.schools.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hcdisat.schools.R
import com.hcdisat.schools.databinding.SchoolItemBinding
import com.hcdisat.schools.models.School

class SchoolsAdapter(
    private var schools: List<School> = listOf(),
    private val onSchoolClick: (dbn: String) -> Unit
): RecyclerView.Adapter<SchoolsItemViewHolder>() {

    fun setSchools(newSchools: List<School>) {
        schools = newSchools
        notifyItemRangeChanged(0, itemCount - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolsItemViewHolder =
        SchoolsItemViewHolder(
            SchoolItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onSchoolClick
        )

    override fun onBindViewHolder(holder: SchoolsItemViewHolder, position: Int) =
        holder.bind(schools[position])

    override fun getItemCount(): Int = schools.size
}

class SchoolsItemViewHolder(
    private val binding: SchoolItemBinding,
    private val onSchoolClick: (dbn: String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(school: School) {
        with(binding) {
            dbn.text = school.dbn
            schoolName.text = school.schoolName
            totalStudents.text = school.totalStudents
            schoolSports.text = school.schoolSports
                ?: binding.root.context.getString(R.string.no_sports)

            root.setOnClickListener { onSchoolClick(school.dbn) }
        }
    }
}