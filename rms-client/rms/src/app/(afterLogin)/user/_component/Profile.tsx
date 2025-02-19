"use client"

import style from "@/app/(afterLogin)/user/_component/profile.module.css";
import * as React from "react";
import {useCallback, useEffect, useState} from "react";
import GreenButton from "@/app/_component/GreenButton";
import InputBox from "@/app/_component/InputBox";
import {getUser} from "@/app/(afterLogin)/user/_api/getUser";
import {User} from "@/model/User";
import {useSession} from "next-auth/react";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {patchUser} from "@/app/(afterLogin)/user/_api/patchUser";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import SelectBox from "@/app/_component/SelectBox";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

export default function Profile() {
    const {data: session, status} = useSession();
    const [user, setUser] = useState<User>();
    const [selectBoxOptions, setSelectBoxOptions] = useState<SelectBoxOption[]>([]);
    const [selectOption, setSelectOption] = useState<SelectBoxOption>();
    const showAlert = CallAlertDialog();

    const fetchUser = useCallback( async () => {
        if(session?.user?.id && status === "authenticated"){
            const res = await getUser(session.user.id)
            if(res.ok) {
                const data = await res.json()
                setUser(data as User)
            }
            else showAlert("fail")
        }
    },[session?.user.id, status]);

    const fetchCountryCodes = useCallback(async () => {
        const response = await fetch("https://restcountries.com/v3.1/all");
        const json = await response.json()
        return json
            .map((country: any) => ({
                name: country.name.common,
                value: country.idd.root + (country.idd.suffixes?.[0] || "")
            }))
            .filter((country: any) => country.name && country.value)
            .sort((a: any, b: any) => a.name.localeCompare(b.name));
    },[]);

    const handleChange = (path: string, value: string) => {
        setUser(prevState => ({
            ...prevState,
            ...setUserKeyValue({ ...prevState }, path, value)
        }));
    };

    const setUserKeyValue = (obj: any, path: string, value: any) => {
        const newObj = { ...obj };
        newObj[path] = value;
        return newObj;
    };

    const handleOnClickSave = async () => {
        if (!user) {
            showAlert("Please correct the word");
            return;
        }
        const res = await patchUser(user)
        if (res.status === 200) {
            showAlert("success!");
        } else {
            showAlert("fail!");
        }
    }

    const loadCountries = async () => {
        try {
            const countryData = await fetchCountryCodes();
            if (!Array.isArray(countryData) || countryData.length === 0) {
                setSelectBoxOptions([]);
                return;
            }
            setSelectBoxOptions(countryData);
        } catch (error) {
            console.error("Error fetching country codes:", error);
            setSelectBoxOptions([]);
        }
    };

    useEffect(() => {
        fetchUser();
        loadCountries();
    }, [fetchUser, fetchCountryCodes]);

    useEffect(() => {
        if (selectBoxOptions.length > 0 && !selectOption) {
            setSelectOption(selectBoxOptions[0]);
        }
        if (user?.phone_number) {
            const phoneParts = user.phone_number.split('/');
            const countryCode = phoneParts[0];

            const selectedOption =
                selectBoxOptions.find(option => option.value === countryCode);
            if (selectedOption) {
                setSelectOption(selectedOption);
            }
        }
    }, [selectBoxOptions]);


    return (
        <>
            {session?.user? (
                <div className={style.container}>
                    <div className={style.header}>
                        User setting
                    </div>
                    <section className={style.section}>
                        <div className={style.subTitle}>
                            Sub Title
                        </div>
                        <div className={style.input}>
                            <div className={style.inputBox}>
                                <InputBox disabled={true} label={"ID"} value={user?.id}/>
                            </div>
                            <div>
                                <InputBox disabled={true} label={"NAME"} value={user?.name || ""}/>
                            </div>
                        </div>
                    </section>
                    <section className={style.middleSection}>
                        <div className={style.subTitle}>
                            Sub Title
                        </div>
                        <div className={style.input}>
                            <div className={style.inputBox}>
                                <InputBox label={"EMAIL"} value={user?.email || ""}
                                          onChange={(value) => handleChange('email', value)}/>
                            </div>
                            <div className={style.phone}>
                                <SelectBox
                                    width={"200px"}
                                    value={selectOption ? `${selectOption.name}/ ${selectOption.value}` : "Please Refresh"}
                                    options={selectBoxOptions}
                                    label={"PHONE-NUMBER"}
                                    onChange={(option) => {
                                        setSelectOption(option);
                                        handleChange('phone_number', `${option.value}/ `);
                                    }}
                                />
                                <div className={style.phoneNumber}>
                                    <InputBox value={user?.phone_number} placeHolder={"Enter Phone-Number"}
                                        onChange={(value) => handleChange('phone_number', value)}
                                    />
                                </div>
                            </div>
                        </div>
                    </section>
                    <section className={style.section}>
                        <div className={style.detailSentence}>
                            <div className={style.detailFName}>
                            To update your Personal details, including FName and LName, contact our
                            </div>
                            <div className={style.detailSupport}>
                                &nbsp;support team.
                            </div>
                        </div>
                        <div className={style.saveButton}>
                            <GreenButton name={"Save"} onClick={handleOnClickSave}/>
                        </div>
                    </section>
                </div>
            ) : (
                <div className={style.loading}>
                    <Loading/>
                </div>
            )}
        </>
    );
}
