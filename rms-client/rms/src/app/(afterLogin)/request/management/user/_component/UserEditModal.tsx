'use client';

import React, {useEffect, useState} from "react";
import style from './userEditModal.module.css';
import {faTrash, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import SelectSearchBox from "@/app/(afterLogin)/request/management/_component/SelectSearchBox";
import {User} from "@/model/User";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import {getUser} from "@/app/(afterLogin)/request/management/user/_api/getUser";
import {patchService} from "@/app/(afterLogin)/request/management/service/_api/patchService";
import {patchUser} from "@/app/(afterLogin)/request/management/user/_api/patchUser";


type Props = {
    userId: string;
    closeModal: () => void;
}

export default function UserEditModal({userId, closeModal}: Props) {
    const [user, setUser] = useState<User>();
    const [selectedAddService, setSelectedAddService] = useState<SelectBoxOption | null>(null);

    const fetchUser = async (userId: string) => {
        const response = await getUser(userId);
        const data = await response.json();
        const service = data as User
        setUser(service);
        return service
    }
    const updateUser = async () => {
        const response = await patchUser(user!);
        if (response.ok) {
            alert("Update successful")
            closeModal()
        } else alert("Fail update")
    }

    const handleUserServiceInsertClick = async () => {
        setUser((prev) =>({
            ...prev,
            services: [
                ...(prev?.services ||[]),
                {
                    id: selectedAddService?.value,
                    name: selectedAddService?.name
                }
            ]
        }) as User)
    }

    const handleUserServiceDeleteClick = async (serviceId: string) => {
        setUser((prev) =>({
            ...prev,
            services: (prev?.services || []).filter(
                (service) => service.id !== serviceId
            )
        }) as User)
    }

    useEffect(() => {
        fetchUser(userId);
    }, []);

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={style.modalCloseButton}/>
                <div className={style.modalTitle}>User Management</div>
                <div className={style.formGroup}>
                    <InputBox
                        label={"Id"}
                        value={user?.id}
                        disabled={true}
                    />
                    <InputBox
                        label={"Name"}
                        value={user?.name}
                        disabled={true}
                    />
                </div>
                <div className={style.contentGroup}>
                    <div className={style.content}>
                        <div className={style.contentTitle}>Services</div>
                        <div className={style.contentFormGroup}>
                            <SelectSearchBox
                                type={'service'}
                                onSelect={setSelectedAddService}
                                width={'15vw'}/>
                            <button className={style.addButton} onClick={handleUserServiceInsertClick}>Add</button>
                        </div>
                        <table className={style.table}>
                            <thead>
                            <tr>
                                <th>Id</th>
                                <th>Name</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            {user?.services?.map((service, index) => (
                                <tr key={index}>
                                    <td>{service.id}</td>
                                    <td>{service.name}</td>
                                    <td>
                                        <FontAwesomeIcon
                                            className={style.deleteButton}
                                            icon={faTrash}
                                            onClick={() => handleUserServiceDeleteClick(service.id!)}
                                        />
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className={style.buttonGroup}>
                    <GreenButton name={"Cancel"} onClick={closeModal}/>
                    <BlueButton name={'Confirm'} onClick={() => updateUser()}/>
                </div>
            </div>
        </div>
    )
}