package com.salton123.lib_demo

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.bin.david.form.data.format.draw.FastTextDrawFormat
import com.bin.david.form.data.table.ArrayTableData
import com.salton123.fundation.FundAppDatabase
import com.salton123.fundation.bean.CodeStocksInnerJoinInfo
import com.salton123.fundation.mvvm.FundationViewModel
import com.salton123.fundation.util.AssetsUtils
import com.salton123.soulove.common.Constants
import com.salton123.soulove.lib_demo.R
import com.salton123.ui.base.BaseActivity
import kotlinx.android.synthetic.main.page_fundation.*
import kotlinx.android.synthetic.main.search_title.*
import java.io.File

/**
 * User: wujinsheng1@yy.com
 * Date: 2020/9/4 18:05
 * ModifyTime: 18:05
 * Description:
 */
@Route(path = Constants.Router.Fundation.MAIN)
class FundationPage : BaseActivity() {
    override fun getLayout(): Int = R.layout.page_fundation
    lateinit var mFundViewModel: FundationViewModel
    override fun enableTitleBar(): Boolean = false
    override fun initViewAndData() {
        mFundViewModel = ViewModelProviders.of(this).get(FundationViewModel::class.java)
        mFundViewModel.mCodeStacksRet.observe(this, Observer {
            fillDataToTable(it)
        })
        smartTable.setZoom(true, 2f, 0.4f)
        if (File(FundAppDatabase.DB_PATH).exists()) {
            FundAppDatabase.init(this)
        } else {
            copyDatabase()
            FundAppDatabase.init(this)
//            longToast("请重启app")
        }
    }

    private fun copyDatabase() {
        AssetsUtils.copyAssetFile(this,
                FundAppDatabase.DB_PATH.replace("fund.db", "")
                , "fund.db")
    }

    private fun fillDataToTable(codeStacks: MutableList<CodeStocksInnerJoinInfo>) {
        if (codeStacks.isEmpty()) {
            return
        }
        if (smartTable.tableData != null) {
            smartTable.tableData.clear()
        }
        val tableName = "${codeStacks[0].gpjc} - ${codeStacks[0].gpdm}"
//        val titleName = arrayOf("基金名称", "基金代码", "股票名称", "股票代码")
        val titleName = arrayOf("基金名称", "基金代码")

        val data: Array<Array<String>> = arrayOf(
                codeStacks.map { it.shortname }.toTypedArray(),
                codeStacks.map { it.fcode }.toTypedArray()
//                ,
//                codeStacks.map { it.gpjc }.toTypedArray(),
//                codeStacks.map { it.gpdm }.toTypedArray()
        )
        smartTable.tableData = ArrayTableData.create(tableName, titleName, data, FastTextDrawFormat<String>())
        smartTable.matrixHelper.reset()
        smartTable.visibility = View.VISIBLE
    }

    override fun initListener() {
        super.initListener()
        setListener(tvTitleSearch)
        etInput.setOnEditorActionListener { _, actionId, event ->
            //当actionId == XX_SEND 或者 XX_DONE时都触发
            //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
            //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null &&
                    KeyEvent.KEYCODE_ENTER == event.keyCode &&
                    KeyEvent.ACTION_DOWN == event.action) { //处理事件
                startSearch()
            }
            false
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            tvTitleSearch -> {
                startSearch()
            }
        }
    }

    private fun startSearch() {
        val inputMsg = etInput.text.toString().trim()
        if (inputMsg.isEmpty()) {
            shortToast(getString(R.string.hint_search_fund_stocks))
        } else {
            mFundViewModel.searchFundHoldingStocks(inputMsg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}