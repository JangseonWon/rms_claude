'use client';

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/service/_component/serviceEditModal.module.css";
import {faTrash, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import {Categories} from "@/model/Categories";
import {getCategories} from "@/app/(afterLogin)/request/management/service/_api/getCategories";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import SelectSearchBox from "@/app/(afterLogin)/request/management/_component/SelectSearchBox";
import {postSampleType} from "@/app/(afterLogin)/request/management/service/_api/postSampleType";
import {putExtension} from "@/app/(afterLogin)/request/management/service/_api/postExtensions";
import {deleteSampleType} from "@/app/(afterLogin)/request/management/service/_api/deleteSampleType";
import {deleteExtension} from "@/app/(afterLogin)/request/management/service/_api/deleteExtension";
import {patchService} from "@/app/(afterLogin)/request/management/service/_api/patchService";
import {Service} from "@/model/Service";
import {getService} from "@/app/(afterLogin)/_api/getService";
import {isThenable} from "next/dist/client/components/router-reducer/router-reducer-types";


type Props = {
    serviceId: string
    closeModal: () => void;
    refreshData: () => void;
}

export default function ServiceEditModal({serviceId, closeModal, refreshData}: Props) {
    const [selectedCategoryName, setSelectedCategoryName] = useState<string>();
    const [selectedCategoryId, setSelectedCategoryId] = useState<string>('');
    const [service, setService] = useState<Service>();
    const [categories, setCategories] = useState<SelectBoxOption[]>([]);
    const [required, setRequired] = useState<boolean>(false);
    const [selectedSampleType, setSelectedSampleType] = useState<SelectBoxOption | null>(null);
    const [selectedExtension, setSelectedExtension] = useState<SelectBoxOption | null>(null);

    const requiredOption = [
        {name: "True", value: true},
        {name: "False", value: false}
    ]

    const transformCategoryToOptions = (data: Categories[]): SelectBoxOption[] => {
        return data.map(value => ({
            value: value.id,
            name: value.name
        }));
    };
    const fetchServiceData = async (serviceId: string) => {
        const response = await getService(serviceId);
        const data = await response.json();
        const service = data as Service
        setService(service);
        return service
    }

    const fetchCategoryData = async () => {
        const response = await getCategories();
        const data = await response.json();
        setCategories(transformCategoryToOptions(data as Categories[]));
    }
    useEffect(() => {
        fetchCategoryData();
        fetchServiceData(serviceId)
            .then((service) => setSelectedCategoryName(service.category?.name)
        )
    }, []);
    const handleServiceCategoryChangeClick = async () => {
        if (service && selectedCategoryId) {
            const serviceData : Service = {id: service.id, category_id: selectedCategoryId};
            await patchService(serviceData);
            refreshData();
        }
    }

    const handleSampleTypeAddClick = async () => {
        if (selectedSampleType && service) {
            const sampleTypeData = {service_id : service.id, sample_type_id: selectedSampleType?.value};
            await postSampleType(sampleTypeData);
        }
    }

    const handleSampleTypeDeleteClick = async (sampleTypeId: string) => {
        if (service) {
            await deleteSampleType(service.id, sampleTypeId);
        }
    }

    const handleExtensionAddClick = async () => {
        if (selectedExtension && service) {
            const extensionData = {service_id : service.id, extension_id: selectedExtension?.value, required: required};
            await putExtension(extensionData);
        }
    }

    const handleExtensionDeleteClick = async (extensionId: string) => {
        if (service) {
            await deleteExtension(service.id, extensionId);
        }
    }

    const handleSelectRequiredChange = (value: boolean) => {
        setRequired(value);
    }

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <section className={style.modalHeader}>
                    <div className={style.modalClose} onClick={closeModal}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </div>
                    <div className={style.modalTop}>
                        <div className={style.title}>
                            Edit Service
                        </div>
                    </div>
                </section>
                <section className={style.modalBody}>
                    <section className={style.topBody}>
                        <InputBox
                            label={"Service Id"}
                            value={service?.id}
                            disabled={true}
                            type="categoryName"
                        />
                        <InputBox
                            label={"Service Name"}
                            value={service?.name}
                            disabled={true}
                            type="categoryName"
                        />
                        <div className={style.categoryBox}>
                            <SelectBox
                                label={"Category Name"}
                                value={selectedCategoryName}
                                options={categories}
                                onChange={(value) => {
                                    setSelectedCategoryName(value.name!);
                                    setSelectedCategoryId(value.value);
                                }}
                            />
                        </div>
                        <button className={style.addButton}
                                onClick={() => handleServiceCategoryChangeClick()}
                        >
                            Category Change
                        </button>
                    </section>
                    <section className={style.bottomBody}>
                        <div className={style.leftBody}>
                            <div className={style.innerBody}>
                                <div className={style.typeAndExtension}>
                                    <div className={style.AddContainer}>
                                        <SelectSearchBox type={'sampleType'} onSelect={setSelectedSampleType} width={'11vw'}/>
                                        <button className={style.addButton}
                                                onClick={()=> handleSampleTypeAddClick()}
                                        >
                                            Add
                                        </button>
                                    </div>
                                    <h2>Sample Type</h2>
                                    <table className={style.table}>
                                        <thead>
                                        <tr>
                                            <th>Code</th>
                                            <th>Name</th>
                                            <th>Delete</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {service?.sample_types?.map((sampleType, index) => (
                                            <tr key={index}>
                                                <td>{sampleType.id}</td>
                                                <td>{sampleType.name}</td>
                                                <td>
                                                    <FontAwesomeIcon
                                                        className={style.deleteButton}
                                                        icon={faTrash}
                                                        onClick={() => handleSampleTypeDeleteClick(sampleType.id)}
                                                    />
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div className={style.rightBody}>
                            <div className={style.innerBody}>
                                <div className={style.typeAndExtension}>
                                    <div className={style.AddContainer}>
                                        <SelectSearchBox type={'extension'} onSelect={setSelectedExtension} width={'11vw'}/>
                                        <div className={style.required}>
                                            <SelectBox value={required ? "True" : "False"} options={requiredOption} label={"required"} onChange={(selectedOption) => {
                                                handleSelectRequiredChange(selectedOption.value);
                                            }}/>
                                        </div>
                                        <button
                                            className={style.addButton}
                                            onClick={()=> handleExtensionAddClick()}
                                        >
                                            Add
                                        </button>
                                    </div>
                                    <h2>Extension</h2>
                                    <table className={style.table}>
                                        <thead>
                                        <tr>
                                            <th>Code</th>
                                            <th>Name</th>
                                            <th>Required</th>
                                            <th>Delete</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {service?.extensions?.map((extension, index) => (
                                            <tr key={index}>
                                                <td>{extension.id}</td>
                                                <td>{extension.name}</td>
                                                <td>{extension.required ? "Yes" : "No"}</td>
                                                <td>
                                                    <FontAwesomeIcon
                                                        className={style.deleteButton}
                                                        icon={faTrash}
                                                        onClick={() => handleExtensionDeleteClick(extension.id!)}
                                                    />
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </section>
                </section>
            </div>
        </div>
    )
}