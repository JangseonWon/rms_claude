'use client';

import React, {useState} from "react";
import style from '@/css/modal.module.css';
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {User} from "@/model/User";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {addManager} from "@/app/(afterLogin)/request/management/user/_api/addManager";


type Props = {
    closeModal: () => void;
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "organization", column: "name", name: "LIMS", value: "123" },
    { table: "organization", column: "name", name: "해외사업팀", value: "124" }
];

export default function ManagerAddModal({closeModal}: Props) {
    const [user, setUser] = useState<User>({
        id: "",
        name: "",
        password: "",
        email: "",
        branch_name: "LIMS",
        branch_serial: "123"
    });
    const [selectOption, setSelectOption] = useState<SelectBoxOption>({ table: "organization", column: "name", name: "LIMS", value: "123" });
    const [message, setMessage] = useState<string | null>(null);

    const insertManager = async () => {
        const response = await addManager(user);
        if (response.ok) {
            alert("Manager Add Successful");
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
            user.password &&
            user.email &&
            user.branch_name &&
            user.branch_serial
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
                        label={"Password"}
                        type="password"
                        onChange={(value) => handleChange("password", value)}
                    />
                    <SelectBox
                        width={'200px'}
                        value={selectOption.name}
                        options={selectBoxOptions}
                        label={"Institution"}
                        onChange={(selectedOption) =>{
                            setSelectOption(selectedOption);
                            handleChange("branch_name", selectedOption.name);
                            handleChange("branch_serial", selectedOption.value);
                        }}
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
                {message && <div className={style.message} dangerouslySetInnerHTML={{ __html: message }} />}
                <div className={style.buttonGroup}>
                    <GreenButton name={"Cancel"} onClick={closeModal}/>
                    <BlueButton name={'Add'} onClick={() => insertManager()} disabled={!isAllRequiredFilled()}/>
                </div>
            </div>
        </div>
    )
}