import style from "./qnaHeader.module.css";

type Props = {
    setQnaPage: (component: string) => void;
};

export default function QnaHeader({ setQnaPage }: Props) {
    return (
     <div className={style.qnaHeader}>
         <div className={style.title}>Need Help?</div>
         <div className={style.qnaBody}>
             <span onClick={() => setQnaPage('Notice')}>Notice</span>
             <span onClick={() => setQnaPage('FAQ')}>FAQ</span>
             <span onClick={() => setQnaPage('Q&A')}>Q&A</span>
         </div>
     </div>
    )
}