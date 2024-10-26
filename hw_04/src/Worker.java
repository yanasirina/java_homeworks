class Worker extends Student {
    private double salary;

    public Worker(String name, int age, double salary) {
        super(name, age);
        this.salary = salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "Name: " + getName() + ", Age: " + getAge() + ", Expected Salary: " + getSalary();
    }
}