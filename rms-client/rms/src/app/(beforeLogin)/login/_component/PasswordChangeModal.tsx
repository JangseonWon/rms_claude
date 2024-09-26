'use client';

import React from "react";
import style from './passwordChangeModal.module.css';
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import BlueButton from "@/app/_component/BlueButton";


type Props = {
    closeModal: () => void;
}

export default function PasswordChangeModal({closeModal}: Props) {
    const getMail = () => {
        alert("ok");
    }

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <section className={style.modalHeader}>
                    <div className={style.modalClose} onClick={closeModal}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </div>
                </section>
                <section className={style.modalBody}>
                    <InputBox

                        label={"User Id"}
                        // value={}
                    />
                    <InputBox
                        label={"E-Mail"}
                        // value={}
                    />
                    <BlueButton name={"Send Mail"} onClick={getMail}/>
                    <div>
                        <label className={style.modalText}>
                            Enter your user account and email
                            <br/>
                            and click the button.
                        </label>
                    </div>
                </section>
            </div>
        </div>
)
}