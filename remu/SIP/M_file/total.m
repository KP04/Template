disp('-------- total results ---------')
correct = correct_a + correct_i + correct_u + correct_e + correct_o
error = error_a + error_i + error_u + error_e + error_o
Acc_rate = (correct / (numdata_test * 5)) * 100
