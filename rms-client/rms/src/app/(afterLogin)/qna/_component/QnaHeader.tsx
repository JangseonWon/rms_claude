import style from "./qnaHeader.module.css";
import {useState} from "react";

type Props = {
    setQnaPage: (component: string) => void;
};

export default function QnaHeader({ setQnaPage }: Props) {
    const [selectedPage, setSelectedPage] = useState<string>('Notice');

    const handleClick = (page: string) => {
        setQnaPage(page);
        setSelectedPage(page);
    };

    return (
     <div className={style.qnaHeader}>
         <div className={style.title}>Need Help?</div>
         <div className={style.qnaBody}>
             <span
                 onClick={() => handleClick("Notice")}
                 className={selectedPage === "Notice" ? style.active : ""}
             >
                    Notice
                </span>
             <span
                 onClick={() => handleClick("FAQ")}
                 className={selectedPage === "FAQ" ? style.active : ""}
             >
                    FAQ
                </span>
             <span
                 onClick={() => handleClick("Q&A")}
                 className={selectedPage === "Q&A" ? style.active : ""}
             >
                    Q&A
                </span>
         </div>
     </div>
    )
}