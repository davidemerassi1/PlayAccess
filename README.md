# PlayAccess

**PlayAccess** è il mio progetto di **tesi triennale** presso il laboratorio **EveryWare** dell'Università degli Studi di Milano.

L’applicazione permette a utenti con **disabilità agli arti superiori** di giocare ai loro giochi preferiti su dispositivi Android utilizzando **esclusivamente i movimenti della faccia**.  

La particolarità di PlayAccess è che **non richiede giochi sviluppati appositamente per modalità di controllo alternative**, ma inietta input touchscreen alle applicazioni sottostanti in base alle azioni eseguite fisicamente.  
Il funzionamento si basa su un’associazione **azione eseguita → evento proiettato sullo schermo**.

## Tecnologie principali

- **ML Kit** e **MediaPipe**: riconoscimento dell'angolo di rotazione della faccia e delle espressioni del volto.  
- **Accessibility Services di Android**: iniezione di input alle applicazioni sottostanti.

## Demo

Viene mostrato come è possibile utilizzare il gioco Slither.io esclusivamente muovendo la testa.

![PlayAccess Demo](demo.gif)

## Crediti

Questo lavoro è basato su un progetto precedente di **Matteo Manzoni**, che ha sviluppato la parte di riconoscimento delle espressioni facciali statiche utilizzando i landmark estratti da MediaPipe, permettendo agli utenti di registrare le espressioni a loro più comode.
