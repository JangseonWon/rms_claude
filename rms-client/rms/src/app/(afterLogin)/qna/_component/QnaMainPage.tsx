"use client"


import React, {useState} from "react";
import QuestionTable from "@/app/(afterLogin)/qna/question/_component/QuestionTable";
import style from "@/app/(afterLogin)/user/page.module.css";
import QnaHeader from "@/app/(afterLogin)/qna/_component/QnaHeader";
import NoticeTable from "@/app/(afterLogin)/qna/notice/_component/NoticeTable";
import FaqTable from "@/app/(afterLogin)/qna/faq/_component/FaqTable";

export default function QnaMainPage() {
    const [currentComponent, setCurrentComponent] = useState('Notice');

    const renderComponent = () => {
        switch (currentComponent) {
            case 'Notice':
                return <NoticeTable/>;
            case 'FAQ':
                return <FaqTable/>;
            case 'Q&A':
                return <QuestionTable/>;
            default:
                return <NoticeTable/>;
        }
    };

    return (
        <div>
            <div className={style.headerTag}>
                <QnaHeader setQnaPage={setCurrentComponent}/>
            </div>
            <div className={style.component}>
                {renderComponent()}
            </div>
        </div>
    )
}