'use client';

import React, {useState} from "react";
import style from '@/css/modal.module.css';
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {User} from "@/model/User";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import {addManager} from "@/app/(afterLogin)/request/management/user/_api/addManager";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {Role} from "@/model/Role";


type Props = {
    closeModal: () => void;
}

export default function ManagerAddModal({closeModal}: Props) {
    const [user, setUser] = useState<User>({
        id: "",
        name: "",
        password: "",
        email: "",
        employee_department: ""
    });
    const [message, setMessage] = useState<string | null>(null);
    const showAlert = CallAlertDialog();

    const insertManager = async () => {
        const response = await addManager(user);
        if (response.ok) {
            showAlert("Manager Add Successful");
            closeModal();
        } else {
            const errorMessage = await response.text();

            if (errorMessage === "Password does not meet the required criteria.") {
                setMessage(`
                    Password does not meet the required criteria:<br>
                - At least one uppercase letter<br>
                - At least one lowercase letter<br>
                - At least one special character (e.g., !@#$%^&*()_+-=[]{}|;:',.<>?/)<br>
                - Minimum length of 10 characters
                `);
            } else {
                setMessage(errorMessage);
            }
        }
    };

    const handleChange = (field: keyof User, value: string) => {
        setUser((prev) => ({
            ...prev,
            [field]: value,
        }) as User);
    };

    const isAllRequiredFilled = () => {
        return (
            user.id &&
            user.name &&
            user.email &&
            user.employee_department
        );
    };

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={style.modalCloseButton}/>
                <div className={style.modalTitle}>Manager Add</div>
                <div className={style.formGroup}>
                    <InputBox
                        label={"Id"}
                        onChange={(value) => handleChange("id", value)}
                    />
                    <InputBox
                        label={"Name"}
                        onChange={(value) => handleChange("name", value)}
                    />
                </div>
                <div className={style.formGroup}>
                    <InputBox
                        label={"Role"}
                        disabled={true}
                        value={"MANAGER"}
                    />
                    <InputBox
                        label={"Institution"}
                        onChange={(value) => handleChange("employee_department", value)}
                    />
                </div>
                <div className={style.formGroup}>
                    <div style={{width: '100%'}}>
                        <InputBox
                            label={"Email"}
                            onChange={(value) => handleChange("email", value)}
                        />
                    </div>
                </div>
                <div className={style.formGroup}>
                    <div style={{width: '100%'}}>
                        <InputBox
                            label={"Phone Number"}
                            onChange={(value) => handleChange("phone_number", value)}
                        />
                    </div>
                </div>
                {message && <div className={style.message} dangerouslySetInnerHTML={{__html: message}}/>}
                <div className={style.buttonGroup}>
                    <GreenButton name={"Cancel"} onClick={closeModal}/>
                    <BlueButton name={'Add'} onClick={() => insertManager()} disabled={!isAllRequiredFilled()}/>
                </div>
            </div>
        </div>
    )
}