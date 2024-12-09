'use client';

import React, {useState} from "react";
import style from "@/app/(afterLogin)/manager/page.module.css";
import OrderDeletePage from "@/app/(afterLogin)/manager/_component/OrderDeletePage";
import ManagerHeader from "@/app/(afterLogin)/manager/_component/ManagerHeader";

export default function Page() {
    const [currentComponent, setCurrentComponent] = useState('OrderDelete');

    const renderComponent = () => {
        switch (currentComponent) {
            case 'OrderDelete':
                return <OrderDeletePage/>;
            default:
                return <OrderDeletePage />;
        }
    };

    return (
        <div className={style.container}>
            <div className={style.headerTag}>
                <ManagerHeader setManagerPage={setCurrentComponent}/>
            </div>
            <div className={style.component}>
                {renderComponent()}
            </div>
        </div>
    );
}