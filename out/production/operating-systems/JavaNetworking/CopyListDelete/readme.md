Имплементирајте сервер-клиент сценарио со користење на TCP протоколот во кое клиентите ќе можат да оперираат со
податочниот систем на серверот. Серверот треба да поддржува повеќе клиенти истовремено.
Притоа, серверот треба да може да одговара на три команди од клиентот:
- ``COPY pateka1/imenafajl1.ext pateka2/imenafajl2.ext``: по добивање на оваа наредба, серверот креира нов фајл и ја копира 
целокупната содржина од првиот во вториот фајл.
- ``LIST``: после оваа порака, серверот треба рекурзивно да му ги излиста на клиентот сите фајлови со екстензија .txt во 
дадениот дифолтен фолдер. Притоа му ги праќа името на фајлот, големината и датумот кога е креиран.
- ``DELETE pateka/imenafajl``: по добивање на оваа порака, серверот треба да го избрише дадениот фајл .
Серверот слуша на порта ``7953``.
Потребните измени направете ги во Starter кодот за да го имплементирате сценариото. На крај, ископирајте го вашето 
решение во текстуалната компоненти каде што се наоѓа Starter кодот.*/