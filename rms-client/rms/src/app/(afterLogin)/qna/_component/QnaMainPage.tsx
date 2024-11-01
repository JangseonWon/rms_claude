"use client"


import React, {useState} from "react";
import QuestionTable from "@/app/(afterLogin)/qna/question/_component/QuestionTable";
import style from "@/app/(afterLogin)/user/page.module.css";
import QnaHeader from "@/app/(afterLogin)/qna/_component/QnaHeader";

export default function QnaMainPage() {
    const [currentComponent, setCurrentComponent] = useState('Notice');

    const renderComponent = () => {
        switch (currentComponent) {
            case 'Notice':
                return <QuestionTable/>;
            case 'FAQ':
                return <QuestionTable/>;
            case 'Q&A':
                return <QuestionTable/>;
            default:
                return <QuestionTable/>;
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