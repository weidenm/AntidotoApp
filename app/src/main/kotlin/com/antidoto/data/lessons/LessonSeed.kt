package com.antidoto.data.lessons

import com.antidoto.data.db.entities.Lesson

/**
 * The 15 micro-lessons that make up the algorithmic-literacy trail (Phase 1.4).
 * Content is intentionally short (a couple of paragraphs) and non-judgmental —
 * Antídoto is a trainer, not a scold.
 */
object LessonSeed {

    val lessons: List<Lesson> = listOf(
        Lesson(
            id = "L01",
            title = "O feed nunca acaba",
            content = "A rolagem infinita foi criada para remover pontos de parada. Sem um " +
                "\"fim de página\", seu cérebro não recebe o sinal natural de que é hora de " +
                "parar.\n\nPerceber isso já é meio caminho: defina você mesmo o ponto final, " +
                "porque o app não vai definir por você.",
            orderIndex = 0,
        ),
        Lesson(
            id = "L02",
            title = "Recompensa variável",
            content = "Às vezes você abre o app e encontra algo ótimo; às vezes, nada. Essa " +
                "imprevisibilidade é a mesma lógica das máquinas de caça-níquel — e é o que " +
                "mais prende a atenção.\n\nO valor não está em cada checagem, mas na expectativa. " +
                "Reconhecer a expectativa reduz o impulso de checar \"só mais uma vez\".",
            orderIndex = 1,
        ),
        Lesson(
            id = "L03",
            title = "Notificações e dopamina",
            content = "Cada alerta é um convite para voltar. Vermelho, som e vibração exploram " +
                "reflexos antigos de atenção a novidades e perigos.\n\nExperimente: desligue " +
                "notificações não essenciais. Você decide quando entrar — não o aparelho.",
            orderIndex = 2,
        ),
        Lesson(
            id = "L04",
            title = "Validação social",
            content = "Curtidas e comentários transformam relações em números. O cérebro trata " +
                "esses números como aprovação do grupo, algo que importava muito para sobreviver.\n\n" +
                "Lembre-se: o número é uma métrica do app, não a medida do seu valor.",
            orderIndex = 3,
        ),
        Lesson(
            id = "L05",
            title = "FOMO: o medo de ficar de fora",
            content = "\"Histórias\" que somem, \"ao vivo\" e contadores criam urgência artificial. " +
                "A sensação de que algo importante está acontecendo agora te puxa de volta.\n\n" +
                "Quase nada é realmente urgente. O que importa ainda estará lá depois.",
            orderIndex = 4,
        ),
        Lesson(
            id = "L06",
            title = "Reprodução automática",
            content = "O próximo vídeo começa sozinho para que você não precise decidir continuar. " +
                "A ausência de decisão é o truque: continuar vira o padrão.\n\nReintroduza a " +
                "decisão: desligue o autoplay e escolha ativamente o que assistir.",
            orderIndex = 5,
        ),
        Lesson(
            id = "L07",
            title = "Padrões escuros (dark patterns)",
            content = "Botões de sair escondidos, \"cancelar\" em cinza, perguntas que confundem: " +
                "são desenhos feitos para te empurrar à escolha que favorece o app.\n\n" +
                "Ao notar fricção estranha para sair ou desativar algo, provavelmente é de propósito.",
            orderIndex = 6,
        ),
        Lesson(
            id = "L08",
            title = "Bolha de filtros",
            content = "O algoritmo mostra mais do que você já curtiu. Com o tempo, seu mundo " +
                "encolhe para aquilo que gera reação em você.\n\nBusque ativamente pontos de vista " +
                "diferentes: a diversidade não vem de graça no feed.",
            orderIndex = 7,
        ),
        Lesson(
            id = "L09",
            title = "Indignação engaja",
            content = "Conteúdo que gera raiva ou medo se espalha mais rápido, porque prende a " +
                "atenção. O sistema aprende que te irritar dá lucro.\n\nAntes de compartilhar no " +
                "calor da emoção, respire. A pausa é sua ferramenta.",
            orderIndex = 8,
        ),
        Lesson(
            id = "L10",
            title = "O tempo distorce",
            content = "\"Só cinco minutos\" viram quarenta sem você perceber. Ambientes sem relógio " +
                "e sem fim borram a passagem do tempo.\n\nUm marcador simples — um alarme, um " +
                "objetivo — devolve a noção de quanto tempo passou.",
            orderIndex = 9,
        ),
        Lesson(
            id = "L11",
            title = "Gamificação e sequências",
            content = "Sequências, medalhas e barras de progresso criam a sensação de que parar é " +
                "\"perder\". A culpa por quebrar a sequência é um gancho.\n\nUse metas a seu favor: " +
                "que a sequência sirva a você, não o contrário.",
            orderIndex = 10,
        ),
        Lesson(
            id = "L12",
            title = "Publicidade personalizada",
            content = "Seus dados alimentam anúncios sob medida, no momento em que você está mais " +
                "propenso a clicar. O produto, muitas vezes, é a sua atenção.\n\nDesconfie de " +
                "\"coincidências\": o que parece leitura de mente costuma ser perfilamento.",
            orderIndex = 11,
        ),
        Lesson(
            id = "L13",
            title = "A armadilha da comparação",
            content = "Feeds mostram os melhores momentos editados dos outros. Comparar sua " +
                "rotina real com o destaque alheio é injusto com você.\n\nLembre: você vê o " +
                "bastidor da sua vida e só o palco da vida dos outros.",
            orderIndex = 12,
        ),
        Lesson(
            id = "L14",
            title = "A economia da atenção",
            content = "Se o serviço é grátis, seu tempo e sua atenção são a moeda. Manter você " +
                "por mais tempo é o objetivo de negócio declarado.\n\nEntender o modelo muda o " +
                "jogo: você passa de produto a pessoa no comando.",
            orderIndex = 13,
        ),
        Lesson(
            id = "L15",
            title = "Retomar o comando",
            content = "Imunidade não é bloquear tudo — é escolher com intenção. Pequenas fricções " +
                "conscientes (desligar autoplay, silenciar alertas, definir horários) devolvem " +
                "o volante para as suas mãos.\n\nVocê chegou ao fim da trilha. O treino continua " +
                "todos os dias, um check-in de cada vez.",
            orderIndex = 14,
        ),
    )
}
