'use client';

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/category/_component/categoryModal.module.css";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {putCategories} from "@/app/(afterLogin)/request/management/category/_api/putCategories";
import {fetchCategories} from "@/app/(afterLogin)/request/management/category/_api/fetchCategories";
import {Categories} from "@/model/Categories";

type Props = {
    category?: Categories;
    open: boolean;
    closeModal: () => void;
}

const serviceOptions: SelectBoxOption[] = [
    {value: 'SINGLE', name: 'SINGLE'},
    {value: 'MULTIPLE', name: 'MULTIPLE'}
];

export default function CategoryModal({category, open, closeModal}: Props) {
    const [categoryName, setCategoryName] = useState('');
    const [selectedCategoryType, setSelectedCategoryType] = useState('');

    useEffect(() => {
        if (category) {
            setCategoryName(category.name);
            setSelectedCategoryType(category.order_type);
        }
    }, [category]);

    const addCategory = async () => {
        if(selectedCategoryType) {
            const response = await putCategories({
                name: categoryName,
                order_type: selectedCategoryType
            });
            return await response.json();
        } else {
            alert("Please Select Category Type")
        }
    }

    const updateCategory = async () => {
        if(selectedCategoryType && category) {
            const response = await fetchCategories({
                id: category.id,
                name: categoryName,
                order_type: selectedCategoryType
            });
            return await response.json();
        } else {
            alert("Error: No Search Category Info")
        }
    }

    const formatAlertMessage = (result: Categories) => {
        return `id = ${result.id}\nname = ${result.name}\norder type = ${result.order_type}`;
    }

    const handleAddButtonClick = async () => {
        const result = await addCategory();
        alert(formatAlertMessage(result));
        closeModal()
    }

    const handleUpdateButtonClick = async () => {
        const result = await updateCategory();
        alert(formatAlertMessage(result));
        closeModal()
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
                            {category ? 'Edit Category' : 'Add Category'}
                        </div>
                    </div>
                </section>
                <section className={style.modalBody}>
                    <div className={style.leftBody}>
                        <div className={style.categoryName}>
                            <InputBox
                                label={"Category Name"}
                                value={categoryName}
                                onChange={setCategoryName}
                                type="categoryName"
                            />
                            <SelectBox
                                label={"Category Type"}
                                value={selectedCategoryType}
                                options={serviceOptions}
                                required={true}
                                onChange={(value) => {
                                    setSelectedCategoryType(value.value);
                                }}
                            />
                        </div>
                    </div>
                    <div className={style.rightBody}>
                        <button
                            className={style.addButton}
                            onClick={category ? handleUpdateButtonClick : handleAddButtonClick}
                        >
                            {category ? 'Edit' : 'Add'}
                        </button>
                    </div>
                </section>
            </div>
        </div>
    )
}