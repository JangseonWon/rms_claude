'use client';

import * as React from 'react';
import {useState} from 'react';
import clsx from 'clsx';
import {css, styled} from '@mui/system';
import {Modal as BaseModal} from '@mui/base/Modal';
import style from "@/app/(afterLogin)/request/services/precision-oncology/_component/orderModal.module.css";
import {useSelectOrganization} from "@/app/(afterLogin)/request/services/precision-oncology/store/useOrganizationStore";
import {useSelectService} from "@/app/(afterLogin)/request/services/precision-oncology/store/useServiceStore";
import {
    useAge,
    useMedicalDepartment,
    useMemo,
    useMrn,
    useName,
    usePhysician,
    useQuantity,
    useType,
    useWard
} from "@/app/(afterLogin)/request/services/precision-oncology/store/useInputOrderStore";
import ModalExtension from "@/app/(afterLogin)/request/services/precision-oncology/_component/ModalExtension";
import {useBirth, useCollection} from "@/app/(afterLogin)/request/services/precision-oncology/store/useDatePickerStore";
import {usePushExtensions} from "@/app/(afterLogin)/request/services/precision-oncology/store/useInputExtensionStore";
import {Order} from "@/model/Order";
import {fetchAddCartAndOrder} from "@/app/(afterLogin)/request/services/precision-oncology/_api/fetchAddCartAndOrder";

export default function OrderModal() {
    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    const organization = useSelectOrganization();
    const service = useSelectService() || { id: 'default_service_id', name: 'Default Service' };
    const name = useName();
    const mrn = useMrn();
    const birthday = useBirth();
    const age = useAge();
    const type = useType();
    const collectionDate = useCollection();
    const quantity = useQuantity();
    const memo = useMemo();
    const medicalDepartment = useMedicalDepartment();
    const ward = useWard();
    const physician = usePhysician();
    const useExtensionArray = usePushExtensions();

    const isAddOrderDisabled = !(organization && service && name && mrn && type && quantity && collectionDate);

    const selectedMonthFormatted = collectionDate.month < 10 ? `0${collectionDate.month}` : `${collectionDate.month}`;
    const selectedDayFormatted = collectionDate.day < 10 ? `0${collectionDate.day}` : `${collectionDate.day}`;

    const order: Order[] = [{
        requests: [{
            service: {
                id: service!.id,
            },
            memo: memo,
            department: medicalDepartment,
            ward: ward,
            physician: physician,
            status: "ORDERED",
            sample: {
                quantity: Number(quantity),
                age: Number(age),
                //sampling_on: `${collectionDate?.year}-${selectedMonthFormatted}-${selectedDayFormatted}`,
                sample_type: {
                    id: type
                },
                patient: {
                    serial: mrn,
                    sex: "M", // 아직 입력하는 곳이 없네여
                    name: name,
                    birth_year: birthday?.year,
                    birth_month: birthday?.month,
                    birth_day: birthday?.day,
                    organization: {
                        id: organization?.id,
                        name: organization?.name
                    }
                },
                extensions: useExtensionArray
            }
        }]
    }];

    const handleOnClickAddToOrder = async () => {
        try {
            await fetchAddCartAndOrder(order);
            alert("test 성공")
        } catch (error) {
            alert("error 실패")
        }
    };

    return (
        <div>
            <TriggerButton type="button" onClick={handleOpen}>
                Order Now
            </TriggerButton>
            <Modal
                open={open}
                onClose={handleClose}
                slots={{ backdrop: StyledBackdrop }}
            >
                <ModalContent>
                    <div className={style.header}>
                        ORDER
                    </div>
                    <section className={style.firstSection}>
                        <div>
                            <div className={style.mainName}>
                                Institution name *
                            </div>
                            <div className={organization ? "" : style.emptySelect}>
                                {organization?.name ? organization.name : "Select Institution Name"}
                            </div>
                        </div>
                    </section>
                    <section className={style.middleSection}>
                        <div>
                            <div className={style.mainName}>
                                Service Info.
                            </div>
                            <div className={service ? style.subName : style.emptySelect}>
                                Service *
                            </div>
                            {service?.name ? service.name : "Select Service"}
                        </div>
                    </section>
                    <section className={style.middleSection}>
                        <div className={style.mainName}>
                            Patient Info.
                        </div>
                        <section className={style.subSection}>
                            <div className={style.subItem}>
                                <div className={name ? style.subName : style.emptySubName}>
                                    Name *
                                </div>
                                {name}
                            </div>
                            <div className={style.subItem}>
                                <div className={mrn ? style.subName : style.emptySubName}>
                                    MRN *
                                </div>
                                {mrn}
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Date of Birth
                                </div>
                                {birthday?.fullDate}
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Age
                                </div>
                                {age}
                            </div>
                        </section>
                    </section>
                    <section className={style.middleSection}>
                        <div className={style.mainName}>
                            Specimen/ .Sample Info.
                        </div>
                        <section className={style.subSection}>
                            <div className={style.subItem}>
                                <div className={type ? style.subName : style.emptySubName}>
                                    Type *
                                </div>
                                {type}
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Date of collection *
                                </div>
                                {collectionDate?.fullDate}
                            </div>
                            <div className={style.subItem}>
                                <div className={quantity ? style.subName : style.emptySubName}>
                                    Quantity *
                                </div>
                                {quantity}
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Memo
                                </div>
                                {memo}
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
                                {medicalDepartment}
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Ward
                                </div>
                                {ward}
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Physician Name
                                </div>
                                {physician}
                            </div>
                        </section>
                    </section>
                    <section className={style.bottomSection}>
                        <ModalExtension/>
                    </section>
                    <div className={style.orderButton}>
                        <button className={isAddOrderDisabled ? style.disabled : style.addOrder}
                                disabled={isAddOrderDisabled}
                                onClick={handleOnClickAddToOrder}>
                            Order
                        </button>
                        <button className={style.cancel} onClick={handleClose}>
                            Cancel
                        </button>
                    </div>
                </ModalContent>
            </Modal>
        </div>
    );
}

const Backdrop = React.forwardRef<
    HTMLDivElement,
    { open?: boolean; className: string }
>((props, ref) => {
    const {open, className, ...other} = props;
    return (
        <div
            className={clsx({'base-Backdrop-open': open}, className)}
            ref={ref}
            {...other}
        />
    );
});

const Modal = styled(BaseModal)`
    position: fixed;
    z-index: 1300;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 200px;
`;

const StyledBackdrop = styled(Backdrop)`
    z-index: -1;
    position: fixed;
    inset: 0;
    background-color: rgb(0 0 0 / 0.5);
    -webkit-tap-highlight-color: transparent;
`;

const ModalContent = styled('div')(
    ({theme}) => css`
        font-family: 'IBM Plex Sans', sans-serif;
        font-weight: 500;
        text-align: start;
        position: relative;
    display: flex;
    flex-direction: column;
    gap: 8px;
    overflow: hidden;
    background-color: #fff;
    border-radius: 15px;
    border: 1px solid #DAE2ED;
    box-shadow: 0 4px 12px rgb(0 0 0 / 0.2);
    padding: 24px;
    color: #1C2025;
  `,
);

const TriggerButton = styled('button')(
    ({ theme }) => css`
    font-weight: 700;
    font-size: 0.875rem;
    padding: 6px 10px;
    width: 110px;
    border-radius: 15px;
    border: 1px solid #182847;
    transition: all 150ms ease;
    cursor: pointer;
    background: linear-gradient(135deg, #182847, #354e6d);
    color: #ffffff;
    margin: 10px;
    box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);

    &:hover {
        background: linear-gradient(135deg, #3c6699, #9ebce6);
        border: 1px solid #9ebce6;
    }
  `,
);