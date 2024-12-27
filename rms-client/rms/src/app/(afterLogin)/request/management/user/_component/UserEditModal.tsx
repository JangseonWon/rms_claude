'use client';

import React, {useEffect, useState} from "react";
import style from '@/css/modal.module.css';
import tableStyle from '@/css/globalTable.module.css';
import {faTrash, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import SearchSelectBox, {Option} from "@/app/_component/SearchSelectBox";
import {User} from "@/model/User";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import {getUser} from "@/app/(afterLogin)/request/management/user/_api/getUser";
import {patchUser} from "@/app/(afterLogin)/request/management/user/_api/patchUser";
import {Service} from "@/model/Service";
import {getServices} from "@/app/(afterLogin)/request/management/user/_api/getServices";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";


type Props = {
    userId: string;
    closeModal: () => void;
    fetchData: () => void;
}

export default function UserEditModal({userId, closeModal, fetchData}: Props) {
    const [user, setUser] = useState<User>();
    const [selectedOption, setSelectedOption] = useState<Option | undefined>();
    const [serviceOptions, setServiceOptions] = useState<Option[]>([])
    const showAlert = CallAlertDialog();
    const [searchTerm, setSearchTerm] = useState('');

    const handleSelect = (option: any) => {
        setSelectedOption(option);
    };

    const fetchUser = async (userId: string) => {
        const response = await getUser(userId);
        if (response.ok) {
            const data = await response.json();
            const service = data as User
            setUser(service);
            return service;
        } else {
            return [];
        }
    }
    const fetchServices = async () => {
        const response = await getServices()
        if (response.ok) {
            const data = await response.json();
            const services = data as Service[]
            const mappedOptions = services.map(mapServiceToOption)
            setServiceOptions(mappedOptions)
        } else {
            return [];
        }
    }
    const mapServiceToOption = (service: Service): Option => ({
        id: service.id!,
        label: service.name!,
    });
    const updateUser = async () => {
        const response = await patchUser(user!);
        if (response.ok) {
            showAlert("Update successful")
            fetchData()
            closeModal()
        } else showAlert("Fail update")
    }

    const handleUserServiceInsertClick = async () => {
        if(selectedOption) {
            setUser((prev) =>{
                const isAlreadyAdded = prev?.services?.some(
                    (service) => service.id === selectedOption.id
                );
                if (isAlreadyAdded) {
                    showAlert("Already registered")
                    return prev;
                }else {
                    setSearchTerm('')
                    return {
                        ...prev,
                        services: [
                            ...(prev?.services ||[]),
                            {
                                id: selectedOption.id,
                                name: selectedOption.label
                            }
                        ]
                    } as User
                }
            })
        }else{
            showAlert('No options selected.');
        }
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
        fetchServices()
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
                <div className={style.formGroup}>
                    <InputBox
                        label={"Email"}
                        value={user?.email}
                        disabled={false}
                        onChange={(value) => {
                            setUser((prev) =>({
                                ...prev,
                                email: value
                            }) as User)
                        }}
                    />
                </div>
                <div className={style.contentGroup}>
                    <div className={style.content}>
                        <div className={style.contentTitle}>Services</div>
                        <div className={style.contentFormGroup}>
                            <SearchSelectBox
                                options={serviceOptions}
                                onSelect={handleSelect}
                                placeholder={'Service name...'}
                                value={searchTerm}
                                onChange={setSearchTerm}
                            />
                            <BlueButton
                                name={'Add'}
                                onClick={handleUserServiceInsertClick}
                            />
                        </div>
                        <table className={tableStyle.table}>
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