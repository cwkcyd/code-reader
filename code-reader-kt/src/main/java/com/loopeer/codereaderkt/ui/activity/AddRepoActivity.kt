package com.loopeer.codereaderkt.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import android.view.View
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ActivityAddRepoBinding
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.ui.view.AddRepoChecker
import com.loopeer.codereaderkt.ui.view.Checker
import com.loopeer.codereaderkt.ui.view.TextWatcherImpl
import com.loopeer.codereaderkt.utils.DownloadUrlParser
import com.loopeer.codereaderkt.utils.FileCache


class AddRepoActivity : BaseActivity(), Checker.CheckObserver {

    private var mAddRepoChecker: AddRepoChecker? = null
    private lateinit var binding: ActivityAddRepoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_repo)
        mAddRepoChecker = AddRepoChecker(this)


        binding.editAddRepoName.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(editable: Editable) {
                super.afterTextChanged(editable)
                mAddRepoChecker!!.repoName = editable.toString()
            }
        })
        binding.editAddRepoUrl.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(editable: Editable) {
                super.afterTextChanged(editable)
                mAddRepoChecker!!.repoDownloadUrl = editable.toString()
            }
        })
    }

    fun onDownClick(view: View) {
        hideSoftInputMethod()
        if (!DownloadUrlParser.parseGithubUrlAndDownload(this@AddRepoActivity, mAddRepoChecker!!.repoDownloadUrl!!.trim())) {
            showMessage(getString(R.string.repo_download_url_parse_error))
        } else {
            val repo = Repo(
                    mAddRepoChecker!!.repoName!!.trim { it <= ' ' }, FileCache().getInstance().getRepoAbsolutePath(mAddRepoChecker!!.repoName!!), mAddRepoChecker!!.repoDownloadUrl!!.trim { it <= ' ' }, true, 0)
            Navigator().startDownloadNewRepoService(this, repo)
            this.finish()
        }
    }

    override fun check(b: Boolean) {
        binding.btnAddRepo.isEnabled = b
    }

}