package blooom.cinnamon.cleanexplorers.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import blooom.cinnamon.cleanexplorers.R
import blooom.cinnamon.cleanexplorers.model.User
import butterknife.BindView
import butterknife.ButterKnife

class HomeFragment : Fragment() {

    @BindView(R.id.welcome_text)
    lateinit var welcomeTextView: TextView



    @SuppressLint("SetTextI18n")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        ButterKnife.bind(this, view)

        val user : User? =arguments!!.getParcelable("user")

        welcomeTextView.text = welcomeTextView.text.toString()+ user!!.firstName +" "+user.lastName
        return view
    }


}