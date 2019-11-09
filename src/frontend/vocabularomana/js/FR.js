class FR {

    constructor() {
        this.currectWordCount = 0;
        this.currentWord = "";
        this.remainingWords = [];
        this.localData = "";
        this.currentTotal = 0;
        this.currentWrong = 0;
        this.showTranslationInitialized = false;
    }

    init() {
        this.localData = this.getLocalWordData();
        document.querySelector("#random").addEventListener("click", e => this.selectChapters(false));
        document.querySelector("#oftenWrong").addEventListener("click", e => this.selectChapters(true));
        document.querySelector("#correctBtn").addEventListener("click", e => this.handleBtnClick(e, true));
        document.querySelector("#wrongBtn").addEventListener("click", e => this.handleBtnClick(e, false));


        const pch = document.cookie.split(";").find(c => c.startsWith("chapters="));
        if (pch != null) {
            const chapterCheckboxes = [...document.querySelectorAll("#chapters input")];
            chapterCheckboxes.filter(ch => pch.includes(ch.value)).forEach(ch => ch.checked = true);
        }
    }

    getOftenWrong(words) {
        const localData = this.getLocalWordData();
        const wrongLocal = localData.words.filter(w => {
            const percentCorrect = ((w.totalCount - w.wrongCount) / w.totalCount) * 100;
            return percentCorrect <= 80;
        });
        words = words.filter(w => {
            const localWord = wrongLocal.find(lw => lw.id == w.id);
            if (localWord == null) {
                const allWord = localData.words.find(lw => lw.id == w.id);
                return allWord == null;
            } else {
                return true;
            }
        });
        console.log("Often wrong words");
        console.log(words);
        return words;
    }

    selectChapters(oftenWrong) {
        this.currentTotal = 0;
        this.currentWrong = 0;
        this.setProgres(0);
        const scoreHolder = document.querySelector("#score");
        scoreHolder.classList.add(`hidden`);
        const chapterCheckboxes = [...document.querySelectorAll("#chapters input")];
        const chapters = chapterCheckboxes.filter(ch => ch.checked).map(ch => ch.value);
        console.log("Selected chapters: " + chapters);
        document.cookie = `chapters=${chapters};path=/;max-age=60*60*24*365;`;
        const chaptersDiv = document.querySelector("#wordButtons");
        chaptersDiv.classList.remove('hidden');
        const that = this;
        this.getWords().then(res => {
            let words = res.filter(w => chapters.includes(w.chapter.id + ""));
            if (oftenWrong) {
                words = this.getOftenWrong(words);
            }
            words = that.shuffle(words);
            console.log(words);
            const wordHolder = document.querySelector("#word");
            wordHolder.classList.remove(`hidden`);
            if (!this.showTranslationInitialized) {
                this.showTranslationInitialized = true;
                wordHolder.addEventListener("click", e => that.showTranslation());
            }
            const [head, ...tail] = words;
            const latinCard = document.querySelector("#word .card__face--front");
            const czechCard = document.querySelector("#word .card__face--back");
            that.currectWordCount = words.length;
            that.currentWord = head;
            that.remainingWords = tail;
            if (head != null) {
                latinCard.innerText = head.latin;
                czechCard.innerText = head.cz;
            } else {
                latinCard.innerText = "Nic nenalezeno";
                czechCard.innerText = "Nic nenalezeno";
            }
            const chaptersDiv = document.querySelector("#chapters");
            chaptersDiv.classList.add('hidden');
        });
    }

    getLocalWordData() {
        const rawLocalData = window.localStorage.getItem("wordData");
        if (rawLocalData != null) {
            return JSON.parse(rawLocalData);
        } else {
            return new LocalData();
        }
    }

    showWord() {
        if (this.remainingWords.length <= 0) {
            const correct = ((this.currentTotal - this.currentWrong) / this.currentTotal) * 100;
            const scoreHolder = document.querySelector("#score");
            scoreHolder.classList.remove(`hidden`);
            scoreHolder.innerText = "Konec - správně: " + correct + "%";
            const chaptersDiv = document.querySelector("#chapters");
            chaptersDiv.classList.remove('hidden');
            const wordButtonsDiv = document.querySelector(`#wordButtons`);
            wordButtonsDiv.classList.add(`hidden`);
            // otocime kartu zpatky
            const card = document.querySelector('.card');
            card.classList.remove('is-flipped');
            // schovame kartu
            card.classList.add("hidden");
            return;
        }
        const latinCard = document.querySelector("#word .card__face--front");
        const czechCard = document.querySelector("#word .card__face--back");
        const [head, ...tail] = this.remainingWords;
        latinCard.innerText = head.latin;
        czechCard.innerText = head.cz;
        this.currentWord = head;
        this.remainingWords = tail;
    }

    showTranslation() {
        const card = document.querySelector('.card');
        card.classList.toggle('is-flipped');
    }

    handleBtnClick(evt, correct) {
        ++this.localData.totalCount;
        let localWord = this.localData.words.find(w => w.id === this.currentWord.id);
        if (localWord == null) {
            localWord = new LocalWord();
            localWord.id = this.currentWord.id;
            this.localData.words.push(localWord);
        }
        ++this.currentTotal;
        ++localWord.totalCount;
        if (!correct) {
            ++this.currentWrong;
            ++localWord.wrongCount;
        }
        window.localStorage.setItem('wordData', JSON.stringify(this.localData));
        const progres = this.remainingWords.length === 0 ? 100 : (this.remainingWords.length / this.currectWordCount) * 100;
        this.setProgres(progres);
        this.showWord();
        const card = document.querySelector('.card');
        card.classList.remove('is-flipped');
    }

    setProgres(percent) {
        const p = document.querySelector("#completed");
        p.style.width = percent + "%";
    }

    getWords() {
        return fetch("/words.json")
            .then(response => {
                if (!response.ok) {
                    throw new Error("HTTP error " + response.status);
                }
                return response.json()
            })
            .then(j => {
                console.log(j);
                return j
            }).then(j => j)
            .catch(e => console.log(e))
    }

    shuffle(a) {
        for (let i = a.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [a[i], a[j]] = [a[j], a[i]];
        }
        return a;
    }
}

class LocalData {
    constructor() {
        this.totalCount = 0;
        this.words = []
    }
}

class LocalWord {
    constructor() {
        this.id = 0;
        this.totalCount = 0;
        this.wrongCount = 0;
    }
}

module.exports = FR;