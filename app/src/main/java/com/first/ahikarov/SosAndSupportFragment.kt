package com.first.ahikarov

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.ahikarov.databinding.SosAndSupportLayoutBinding

class SosAndSupportFragment : Fragment() {

    private var _binding: SosAndSupportLayoutBinding? = null
    private val binding get() = _binding!!

    // משתנים לניהול מצב התרגיל
    private var currentStep = 0
    private var hasNudgedUser = false

    // רשימה שתחזיק את 5 השדות שלנו
    private lateinit var inputsList: List<EditText>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SosAndSupportLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputsList = listOf(
            binding.etInput1,
            binding.etInput2,
            binding.etInput3,
            binding.etInput4,
            binding.etInput5
        )

        updateUI()

        binding.btnNext.setOnClickListener {
            checkAndContinue()
        }
    }

    private fun checkAndContinue() {
        if (currentStep == 0 || currentStep == 6) {
            nextStep()
            return
        }

        var filledCount = 0
        var requiredCount = 0

        for (input in inputsList) {
            if (input.visibility == View.VISIBLE) {
                requiredCount++
                if (input.text.toString().trim().isNotEmpty()) {
                    filledCount++
                }
            }
        }

        if (filledCount < requiredCount && !hasNudgedUser) {
            val missing = requiredCount - filledCount
            val msg = getString(R.string.nudge_msg, filledCount, missing)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            hasNudgedUser = true
        } else {
            nextStep()
        }
    }

    // מונע קריסה אם לוחצים מהר מדי בסוף
    private fun nextStep() {
        if (currentStep < 6) {
            currentStep++
            hasNudgedUser = false
            updateUI()
        }
    }

    private fun updateUI() {
        binding.progressBar.progress = currentStep

        inputsList.forEach {
            it.text.clear()
            it.visibility = View.GONE
        }

        when (currentStep) {
            0 -> { // נשימה
                binding.tvInstructions.text = getString(R.string.step_0_instruction)
                binding.btnNext.text = getString(R.string.btn_ready)
                // מחזירים את הליסנר המקורי למקרה שחזרנו או התחלנו מחדש
                binding.btnNext.setOnClickListener { checkAndContinue() }
            }
            1 -> { // ראייה
                binding.tvInstructions.text = getString(R.string.step_1_instruction)
                binding.btnNext.text = getString(R.string.btn_next)
                showInputs(5, R.string.hint_see)
            }
            2 -> { // מגע
                binding.tvInstructions.text = getString(R.string.step_2_instruction)
                showInputs(4, R.string.hint_feel)
            }
            3 -> { // שמיעה
                binding.tvInstructions.text = getString(R.string.step_3_instruction)
                showInputs(3, R.string.hint_hear)
            }
            4 -> { // ריח
                binding.tvInstructions.text = getString(R.string.step_4_instruction)
                showInputs(2, R.string.hint_smell)
            }
            5 -> { // טעם/רגש
                binding.tvInstructions.text = getString(R.string.step_5_instruction)
                showInputs(1, R.string.hint_good)
            }
            6 -> {
                binding.tvInstructions.text = getString(R.string.step_6_instruction)
                binding.btnNext.text = getString(R.string.btn_finish)

                binding.btnNext.setOnClickListener {
                    showFinishDialog()
                }
            }
        }
    }

    private fun showInputs(count: Int, hintResId: Int) {
        for (i in 0 until count) {
            val editText = inputsList[i]
            editText.visibility = View.VISIBLE
            editText.hint = getString(hintResId, i + 1)
        }
    }

    // הדיאלוג אל מול המשתמש
    private fun showFinishDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_help_title)
            .setMessage(R.string.dialog_help_message)
            .setCancelable(false)
            .setPositiveButton(R.string.btn_call_help) { _, _ ->
                dialHelpNumber()
            }
            .setNegativeButton(R.string.btn_exit_app) { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    // הפונקציה חיוג
    private fun dialHelpNumber() {
        val phoneNumber = "1201" // ער"ן
        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL)
        intent.data = android.net.Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}