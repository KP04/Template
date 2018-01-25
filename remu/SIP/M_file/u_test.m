numdata_test = 6;
count_a = 0;
count_i = 0;
count_u = 0;
count_e = 0;
count_o = 0;

for k = 1: numdata_test
 filename = sprintf('../test/u%d.wav', k);
 u_test_data = wavread(filename);
 c = fix(length(u_test_data)/2);
 u_test_cut = u_test_data(c-127: c+128);
 u_test_rceps = real(ifft(log(abs(fft(u_test_cut)))));
 u_test_cep = u_test_rceps(2:11);

 u2a_dist = norm(a_train_cep - u_test_cep);
 u2i_dist = norm(i_train_cep - u_test_cep);
 u2u_dist = norm(u_train_cep - u_test_cep);
 u2e_dist = norm(e_train_cep - u_test_cep);
 u2o_dist = norm(o_train_cep - u_test_cep);

 min = u2a_dist;
 if min > u2i_dist, min = u2i_dist; end;
 if min > u2u_dist, min = u2u_dist; end;
 if min > u2e_dist, min = u2e_dist; end;
 if min > u2o_dist, min = u2o_dist; end;

 if min == u2a_dist, u_answer = sprintf('%s\n', 'a'); count_a = count_a + 1; k
     x = 'a'
 elseif min == u2i_dist, u_answer = sprintf('%s\n', 'i'); count_i = count_i + 1; k
     x = 'i'
 elseif min == u2u_dist, u_answer = sprintf('%s\n', 'u'); count_u = count_u + 1;
 elseif min == u2e_dist, u_answer = sprintf('%s\n', 'e'); count_e = count_e + 1; k 
     x = 'e'
 elseif min == u2o_dist, u_answer = sprintf('%s\n', 'o'); count_o = count_o + 1; k
     x = 'o'
 end;
end;

disp('-------- u test results ---------')
count_a
count_i
count_u
count_e
count_o
correct_u = count_u
error_u = count_a + count_i + count_e + count_o
Acc_rate = (correct_u / numdata_test) * 100
