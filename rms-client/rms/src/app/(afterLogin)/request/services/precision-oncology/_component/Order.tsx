'use client';

import style from "@/app/(afterLogin)/request/services/precision-oncology/_component/order.module.css"
import DatePickerButton from "@/app/(afterLogin)/request/services/_component/DatePickerButton";
import InputTextField from "@/app/(afterLogin)/request/services/_component/InputTextField";
import SelectOrganization from "@/app/(afterLogin)/request/services/precision-oncology/_component/SelectOrganization";
import SelectService from "@/app/(afterLogin)/request/services/precision-oncology/_component/SelectService";
import InputExtension from "@/app/(afterLogin)/request/services/precision-oncology/_component/InputExtension";

export default function Order() {

    return (
        <>
            <section className={style.firstSection}>
                <div>
                    <div className={style.mainName}>
                        Institution name *
                    </div>
                    <SelectOrganization/>
                </div>
            </section>
            <section className={style.middleSection}>
                <div>
                    <div className={style.mainName}>
                        Service Info.
                    </div>
                    <div className={style.subName}>
                        Service *
                    </div>
                    <SelectService/>
                </div>
            </section>
            <section className={style.middleSection}>
                <div className={style.mainName}>
                    Patient Info.
                </div>
                <section className={style.subSection}>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            Name *
                        </div>
                        <InputTextField/>
                    </div>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            MRN *
                        </div>
                        <InputTextField/>
                    </div>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            Date of Birth
                        </div>
                        <DatePickerButton/>
                    </div>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            Age
                        </div>
                        <InputTextField/>
                    </div>
                </section>
            </section>
            <section className={style.middleSection}>
                <div className={style.mainName}>
                    Specimen/ .Sample Info.
                </div>
                <section className={style.subSection}>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            Type *
                        </div>
                        <InputTextField/>
                    </div>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            Date of collection *
                        </div>
                        <DatePickerButton/>
                    </div>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            Quantity *
                        </div>
                        <InputTextField/>
                    </div>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            Memo
                        </div>
                        <InputTextField/>
                    </div>
                </section>
            </section>
            <section className={style.middleSection}>
                <div className={style.mainName}>
                    Additional Info.
                </div>
                <section className={style.subSection}>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            Medical Department
                        </div>
                        <InputTextField/>
                    </div>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            Ward
                        </div>
                        <InputTextField/>
                    </div>
                    <div className={style.subItem}>
                        <div className={style.subName}>
                            Physician Name
                        </div>
                        <InputTextField/>
                    </div>
                </section>
            </section>
            <section className={style.bottomSection}>
                <InputExtension/>
            </section>
        </>
    )
}