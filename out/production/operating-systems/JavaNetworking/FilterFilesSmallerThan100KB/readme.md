Потребно е да имплементирате сервер-клиент сценарио со користење на TCP протоколот.
Клиентите  ги изминуваат сите документи во даден именик, и ги пребаруваат сите ```.txt``` или ```.csv``` документи кои се ``помали од 100КB``,
а ``поголеми од 10KB``.
Серверот кога ќе прими порака од одреден клиент, пораката ја запишува во датотеката ``clients_data.txt`` која постои локално кај него.
Секоја информација од секој клиент ја чува во нова линија во истиот документ, во следниот формат:

```<IP-address-of-the-client> <port-of-the-client> <number-of-files>```

Потребно е да овозможите праќање на податоци од повеќе клиенти едновремено.
Серверот слуша на порта 3398. Редоследот на праќање на податоците од клиентите не е важен.
Пример:
Изгледот на една линија од текстуалната датотека clients_data.txt кај серверот, по успешна комуникација,
треба да изгледа вака: 127.0.0.1 54665 112