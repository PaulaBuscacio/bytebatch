package net.buscacio.bytebatch;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CustomerMapper implements FieldSetMapper<Customer> {

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");


  @Override
  public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
    Customer customer = new Customer();
    customer.setNome(fieldSet.readString("nome"));
    customer.setCpf(fieldSet.readString("cpf"));
    customer.setAgencia(fieldSet.readString("agencia"));
    customer.setConta(fieldSet.readString("conta"));
    customer.setValor(fieldSet.readDouble("valor"));
    customer.setMesDeReferencia(YearMonth.parse(fieldSet.readString("mesDeReferencia"), formatter).atEndOfMonth());
    customer.setHoraImportacao(LocalDateTime.now());
    return customer;
  }
}
