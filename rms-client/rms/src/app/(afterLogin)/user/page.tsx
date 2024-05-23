'use client';

import React, {useState} from "react";
import Profile from "@/app/(afterLogin)/user/_component/Profile";
import ChangePassword from "@/app/(afterLogin)/user/_component/ChangePassword";
import UserHeader from "@/app/(afterLogin)/user/_component/UserHeader";
import Institutions from "@/app/(afterLogin)/user/_component/Institutions";
import style from "@/app/(afterLogin)/user/page.module.css";

export default function Page() {
    const [currentComponent, setCurrentComponent] = useState('UserPage');

    const renderComponent = () => {
        switch (currentComponent) {
            case 'Profile':
                return <Profile/>;
            case 'ChangePassword':
                return <ChangePassword/>;
            case 'Institutions':
                return <Institutions/>;
            default:
                return <Profile />;
        }
    };

    return (
        <div className={style.container}>
            <div className={style.headerTag}>
                <UserHeader setUserPage={setCurrentComponent}/>
            </div>
            <div className={style.component}>
                {renderComponent()}
            </div>
        </div>
    );
}