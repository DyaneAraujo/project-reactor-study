# Project Reactor Study
Learning about reactive programming.

<h2>Reactive Streams</h2>

* Trabalhando com Reactive Streams que implementa 4 interfaces e quem está provendo essa implementações, precisa que o código seja:

1. Assíncrona - Asynchronous 

* Permite que você continue com a execução do seu programa na thread principal enquanto uma tarefa de longa duração é executada na sua própria thread separadamente da thread principal.

2. Não Bloqueante - Non-blocking 

* É a capacidade de fazer operações de entrada e saída (acessar sistema de arquivos, banco de dados, rede, servidores, etc.) sem que a aplicação fique impedida de executar outras coisas em paralelo.

3. Contrapressão - Backpressure

* Define como regular a transmissão de elementos de fluxo. Em outras palavras, controle quantos elementos o destinatário pode consumir.

<h3>Trabalha com Emissão de Eventos</h3>

1. Publisher - Responsável por emitir os eventos. Ele somente emite se alguém dê Subscriber nele, e então é criado o Subscription após alguém dar o Subscriber. O Publisher chama o Subscriber pelo onNext e é executado até que algumas das chamadas seguintes são executadas, como por exemplo o Publisher enviar todos os objetos gerando Backpressure gerando o onComplete ou quando gera algum erro e é gerando OnError e tanto em um como no outro o Subscriber e Subscription são cancelados.

2. Subscriber 

3. Subscription - É enviado pelo Publisher através do onSubscribe. Ele consegue gerenciar a requisição, informando por exemplo a quantidade de elementos que ele deseja. Ele gerencia o Backpressure

<hr/>

<h3>Subscribing to Mono with Consumer and Error</h3>

* Mono significa que você tem um objeto ou não tem nada, mono ou void.

<h4>Consumer</h4>

* Consumer é possibilidade de executar uma ação no momento da Subscribe.

<h4>Subscribing to Mono with OnMethods</h4>

* doOnSubscribe, doOnRequest, doOnNext, doOnSuccess, doOnError, onErrorResume, e onErrorReturn.

<h4>FlatMap and Map</h4>

* Ambas pegam os elementos de um stream de dados (geralmente uma solução como array ou ArrayList) e cada elemento terá uma ação a ser definida em seguida.

* A diferença que flatMap() consegue fazer isso em streams que possuem dimensões (ele achata os dados para ficar linear), então cada elementos daquela coleção de dados será usado independente de ele estar aninhado nessa coleção.

* Quando você tem dados que estão de forma linear nunca é preciso usá-la.
Vamos dizer que você tenha uma lista de listas, a função map() pegaria as listas internas, mas o que você quer é os elementos dessas listas, então só flatMap() resolve.

* Outro exemplo é ter uma lista de strings e você quer os caracteres. Enquanto map() pegara os textos um por um, flatMap() pegaria os caracteres.

<h4>Function</h4>

* funções em lambdas.

<h4>Fallback to Errors</h4>

* Plano B para resume e return sobre erros.

<h4>Flux</h4>

* Flux significa que você tem um objeto ou N objetos, mono ou vários.

<h4>Backpressure</h4>

* Criação de Backpressure por interface ou abstract.

<h4>Interval</h4>

* Intervalos servem para dar agendamentos e ficar publicando algo durante esse agendamento. Para testar pode se utilizar Virtual Time para testar dentro de uma margem de dias. O Interval é rodado em uma Thread paralela a Thread main. 

<h4>Limit Rate</h4>

* Para limitar as request, parecido com backpressure absctract.

<h4>Hot Publisher e Connectable Flux</h4>

* No momento que você se conecta haverá um evento, sem a necessidade de haver um Subscriber antes.

<h4>AutoConnect</h4>

* O evento só emitido após dois Subscriber por exemplo

<h4>PublishOn and SubscribeOn</h4>

* Operator em execução, em Threads paralelas.


