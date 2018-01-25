numdata_test = 6;
count_a = 0;
count_i = 0;
count_u = 0;
count_e = 0;
count_o = 0;

for k = 1: numdata_test
 filename = sprintf('../test/o%d.wav', k);
 o_test_data = wavread(filename);
 c = fix(length(o_test_data)/2);
 o_test_cut = o_test_data(c-127: c+128);
 o_test_rceps = real(ifft(log(abs(fft(o_test_cut)))));
 o_test_cep = o_test_rceps(2:11);

 o2a_dist = norm(a_train_cep - o_test_cep);
 o2i_dist = norm(i_train_cep - o_test_cep);
 o2u_dist = norm(u_train_cep - o_test_cep);
 o2e_dist = norm(e_train_cep - o_test_cep);
 o2o_dist = norm(o_train_cep - o_test_cep);

 min = o2a_dist;
 if min > o2i_dist, min = o2i_dist; end;
 if min > o2u_dist, min = o2u_dist; end;
 if min > o2e_dist, min = o2e_dist; end;
 if min > o2o_dist, min = o2o_dist; end;

 if min == o2a_dist, o_answer = sprintf('%s\n', 'a'); count_a = count_a + 1; k
     x = 'a'
 elseif min == o2i_dist, o_answer = sprintf('%s\n', 'i'); count_i = count_i + 1; k
     x = 'i'
 elseif min == o2u_dist, o_answer = sprintf('%s\n', 'u'); count_u = count_u + 1; k
     x = 'u'
 elseif min == o2e_dist, o_answer = sprintf('%s\n', 'e'); count_e = count_e + 1; k
     x = 'e'
 elseif min == o2o_dist, o_answer = sprintf('%s\n', 'o'); count_o = count_o + 1;
 end;
end;

disp('-------- o test results ---------')
count_a
count_i
count_u
count_e
count_o
correct_o = count_o
error_o = count_a + count_i + count_u + count_e
Acc_rate = (correct_o / numdata_test) * 100
