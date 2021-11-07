package org.anitab.mentorship.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_members_search.*
import org.anitab.mentorship.R
import org.anitab.mentorship.models.User
import org.anitab.mentorship.utils.Constants
import org.anitab.mentorship.utils.SingletonUserList
import org.anitab.mentorship.view.activities.MainActivity
import org.anitab.mentorship.view.activities.MemberProfileActivity
import org.anitab.mentorship.view.adapters.MembersAdapter

class SearchMembersFragment : BaseFragment() {

    /*private val membersViewModel: MembersViewModel by viewModels()*/
    private lateinit var membersAdapter: MembersAdapter
    private var filterMap = hashMapOf(Constants.SORT_KEY to MembersFragment.SortValues.REGISTRATION_DATE.name)

    companion object {
        fun newInstance() = SearchMembersFragment()
    }

    override fun getLayoutResourceId(): Int = R.layout.fragment_members_search

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).bottomNavigation.visibility = View.GONE

        membersAdapter = MembersAdapter(arrayListOf<User>(), ::openUserProfile)
        membersAdapter.updateUsersList(filterMap, SingletonUserList.userList)

        setMemberSearchView()

        tvCancelSearch.setOnClickListener { goBack() }
    }

    private fun setMemberSearchView() {
        svSearchMembers.apply {
            onActionViewExpanded()
            setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // query runs when search button from soft-keyboard is clicked
                    return false
                }

                override fun onQueryTextChange(keyword: String?): Boolean {
                    // when query runs on text change
                    keyword?.let { member ->
                        val searchedMembers = searchMember(member)
                        try {
                            rvSearchedMembers.adapter =
                                MembersAdapter(searchedMembers, ::openUserProfile)
                        } catch (e: Exception) {
                            // log error
                        }
                    }
                    return false
                }
            })
        }
    }

    private fun searchMember(memberKeyword: String): ArrayList<User> {
        pbSearchMembers.visibility = View.VISIBLE
        val userList = arrayListOf<User>()
        for (user in SingletonUserList.userList) {
            // ""+ to convert String to CharSequence
            if (("" + user.username).contains(memberKeyword, ignoreCase = true)) {
                userList.add(user)
            }
        }
        pbSearchMembers.visibility = View.INVISIBLE
        return userList
    }

    private fun openUserProfile(
        memberId: Int,
        sharedImageView: ImageView,
        sharedTextView: TextView
    ) {
        val intent = Intent(activity, MemberProfileActivity::class.java)
        intent.putExtra(Constants.MEMBER_USER_ID, memberId)
        val imgAnim = Pair.create<View, String>(
            sharedImageView,
            ViewCompat.getTransitionName(sharedImageView)!!
        )

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(baseActivity, imgAnim)

        startActivity(intent, options.toBundle())
    }

    private fun goBack() {
        hideKeyboard(requireView())
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
    }

    override fun onDestroyView() {
        (activity as MainActivity).bottomNavigation.visibility = View.VISIBLE
        super.onDestroyView()
    }

    fun onBackPressed() {
        goBack()
    }
}
